package com.evolutiongaming.stracer

import cats.{Applicative, Monad}
import cats.effect.Clock
import cats.implicits._
import com.evolutiongaming.catshelper.ClockHelper._
import com.evolutiongaming.random.Random
import com.evolutiongaming.stracer.util.FromConfigReaderResult
import com.evolutiongaming.stracer.util.PureConfigHelper._
import com.typesafe.config.Config
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.semiauto.deriveReader

trait Tracer[F[_]] {

  def spanId: F[Option[SpanId]]

  def trace(sampling: Option[Sampling] = None): F[Option[Trace]]
}

object Tracer {

  def apply[F[_]](implicit F: Tracer[F]): Tracer[F] = F

  def empty[F[_]: Applicative]: Tracer[F] = const(none[Trace].pure[F], none[SpanId].pure[F])

  def const[F[_]](trace: F[Option[Trace]], spanId: F[Option[SpanId]]): Tracer[F] = {
    val spanId1 = spanId
    val trace1  = trace
    new Tracer[F] {

      def spanId = spanId1

      def trace(sampling: Option[Sampling]) = trace1
    }
  }


  def of[F[_]: Monad: Clock: Random: FromConfigReaderResult](
    runtimeConf: F[RuntimeConf],
    config: Config
  ): F[Tracer[F]] = {
    def startupConf = ConfigSource
      .fromConfig(config)
      .load[StartupConf]
    for {
      tracerConfig <- startupConf.liftTo[F]
      tracer       <- of(tracerConfig, runtimeConf)
    } yield tracer
  }


  def of[F[_]: Monad: Clock: Random](
    startupConf: StartupConf,
    runtimeConf: F[RuntimeConf]
  ): F[Tracer[F]] = {

    val tracer = if (startupConf.enabled) apply[F](runtimeConf) else empty[F]
    tracer.pure[F]
  }


  def apply[F[_]: Monad: Clock: Random](conf: F[RuntimeConf]): Tracer[F] = {
    new Tracer[F] {

      val spanId = {

        def spanId = for {
          long <- Random[F].long
        } yield {
          SpanId(long).some
        }

        for {
          conf    <- conf
          enabled  = conf.enabled
          spanId  <- if (!enabled) none[SpanId].pure[F] else spanId
        } yield spanId
      }

      def trace(sampling: Option[Sampling]) = {

        def trace = for {
          timestamp <- Clock[F].instant
          long      <- Random[F].long
          int       <- Random[F].int
        } yield {
          val spanId  = SpanId(long)
          val traceId = TraceId(timestamp, int, long)
          Trace(traceId, spanId, timestamp.some, sampling).some
        }

        for {
          conf    <- conf
          enabled  = conf.enabled
          trace   <- if (!enabled) none[Trace].pure[F] else trace
        } yield trace
      }
    }
  }


  final case class StartupConf(enabled: Boolean = false)

  object StartupConf {

    val default: StartupConf = StartupConf()

    implicit val configReaderStartupConf: ConfigReader[StartupConf] = deriveReader
  }


  final case class RuntimeConf(enabled: Boolean = true)

  object RuntimeConf {
    val default: RuntimeConf = RuntimeConf()
  }
}
