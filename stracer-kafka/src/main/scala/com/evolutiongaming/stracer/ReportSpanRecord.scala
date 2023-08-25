package com.evolutiongaming.stracer

import cats.Applicative
import cats.data.{NonEmptyList => Nel}
import cats.effect._
import cats.effect.syntax.all._
import cats.syntax.all._
import com.evolutiongaming.catshelper.{FromTry, Log, LogOf, SerialRef}
import com.evolutiongaming.config.ConfigHelper._
import com.evolutiongaming.skafka.producer.{Producer, ProducerConfig, ProducerOf, ProducerRecord}
import com.evolutiongaming.skafka.{ToBytes, Topic}
import com.typesafe.config.Config
import zipkin2.codec.SpanBytesEncoder

import scala.concurrent.duration._

trait ReportSpanRecord[F[_]] {

  def apply(span: SpanRecord): F[Unit]
}

object ReportSpanRecord {

  def empty[F[_]: Applicative]: ReportSpanRecord[F] = const(().pure[F])

  def const[F[_]](value: F[Unit]): ReportSpanRecord[F] = new ReportSpanRecord[F] {
    def apply(span: SpanRecord) = value
  }

  def of[F[_]: Async: LogOf: FromTry](
    config: Config,
    enabled: F[Boolean],
    producerOf: ProducerOf[F]
  ): Resource[F, ReportSpanRecord[F]] = {

    val reportSpan = for {
      _              <- config.getOpt[Boolean]("enabled").filter(identity)
      producerConfig <- config.getOpt[Config]("kafka.producer").map(ProducerConfig(_))
    } yield {
      val topic            = config.getOpt[String]("topic") getOrElse "jaeger"
      implicit val toBytes = ToBytesOf(SpanBytesEncoder.THRIFT)
      of[F](topic, producerConfig, enabled, producerOf)
    }

    reportSpan getOrElse Resource.pure[F, ReportSpanRecord[F]](empty[F])
  }

  def of[F[_]: Async: LogOf: FromTry](
    topic: Topic,
    producerConfig: ProducerConfig,
    enabled: F[Boolean],
    producerOf: ProducerOf[F]
  )(
    implicit
    toBytes: ToBytes[F, Nel[SpanRecord]]
  ): Resource[F, ReportSpanRecord[F]] = {

    sealed trait State

    object State {

      def running(producer: Option[(Producer[F], F[Unit])]): State = Running(producer)

      def stopped: State = Stopped

      case class Running(producer: Option[(Producer[F], F[Unit])]) extends State

      case object Stopped extends State
    }

    def producer(log: Log[F]) =
      for {
        a <- producerOf.apply(producerConfig).allocated
      } yield {
        val (producer, release) = a
        val release1 = release.handleErrorWith { error =>
          log.error(s"Producer.release failed $error", error)
        }
        (producer, release1)
      }

    def update(stateRef: SerialRef[F, State], enabled: Boolean, log: Log[F]) =
      stateRef.update {
        case State.Stopped => State.stopped.pure[F]
        case state: State.Running =>
          state.producer match {
            case Some((_, release)) if !enabled =>
              for {
                _ <- log.info("disable")
                _ <- release
              } yield {
                State.running(none)
              }

            case None if enabled =>
              val state = for {
                _        <- log.info("enable")
                producer <- producer(log)
              } yield {
                State.running(producer.some)
              }
              state.handleErrorWith { error =>
                for {
                  _ <- log.error(s"Producer failed: $error", error)
                } yield {
                  State.running(none)
                }
              }

            case _ => (state: State).pure[F]
          }
      }

    def background(stateRef: SerialRef[F, State], log: Log[F]) =
      ().tailRecM { _ =>
        for {
          state <- stateRef.get
          result <- state match {
            case State.Stopped => ().asRight[Unit].pure[F]
            case _: State.Running =>
              for {
                enabled <- enabled
                _       <- Sync[F].uncancelable { _ => update(stateRef, enabled, log) }
                _       <- Temporal[F].sleep(10.seconds)
              } yield {
                ().asLeft[Unit]
              }
          }
        } yield result
      }

    val resource = for {
      log      <- LogOf[F].apply(getClass)
      stateRef <- SerialRef[F].of(State.running(none))
      release = stateRef.update {
        case State.Running(Some((_, release))) => release.as(State.stopped)
        case State.Running(None)               => State.stopped.pure[F]
        case State.Stopped                     => State.stopped.pure[F]
      }
      fiber <- background(stateRef, log)
        .guarantee(release)
        .start
    } yield {
      val release1 = {
        val release1 = for {
          _ <- release
          _ <- fiber.join
        } yield {}

        release1.timeoutTo(5.seconds, fiber.cancel)
      }

      val producer = for {
        state <- stateRef.get
      } yield {
        state match {
          case State.Running(producer) => producer.map { case (producer, _) => producer }
          case State.Stopped           => none[Producer[F]]
        }
      }

      val reportSpan = apply(topic, producer, log)
      (reportSpan, release1)
    }

    Resource(resource)
  }

  def apply[F[_]: Concurrent: FromTry](topic: Topic, producer: F[Option[Producer[F]]], log: Log[F])(
    implicit
    toBytes: ToBytes[F, Nel[SpanRecord]]
  ): ReportSpanRecord[F] = new ReportSpanRecord[F] {

    def apply(span: SpanRecord): F[Unit] = {

      def send(producer: Producer[F]) = {
        val record = ProducerRecord[String, Nel[SpanRecord]](topic = topic, key = span.traceId.hex, value = Nel.of(span))
        producer
          .send(record)
          .void
          .handleErrorWith { error =>
            log.error(s"Producer.send $span failed: $error, $error")
          }
          .start
          .void
      }

      for {
        producer <- producer
        result   <- producer.foldMapM(send)
      } yield result
    }
  }
}
