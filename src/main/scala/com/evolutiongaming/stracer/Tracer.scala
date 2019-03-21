package com.evolutiongaming.stracer

import cats.Applicative
import cats.effect.{Clock, Sync}
import cats.implicits._
import com.evolutiongaming.catshelper.ClockHelper._
import com.evolutiongaming.config.ConfigHelper._
import com.evolutiongaming.random.Random
import com.typesafe.config.Config

trait Tracer[F[_]] {

  def spanId: F[Option[SpanId]]

  def trace(parentId: Option[SpanId] = None): F[Option[Trace]]
}

object Tracer {

  def empty[F[_] : Applicative]: Tracer[F] = const(none[Trace].pure[F], none[SpanId].pure[F])

  def const[F[_]](trace: F[Option[Trace]], spanId: F[Option[SpanId]]): Tracer[F] = {
    val spanId1 = spanId
    val trace1 = trace
    new Tracer[F] {
      
      def spanId = spanId1

      def trace(parentId: Option[SpanId]) = trace1
    }
  }

  def of[F[_] : Sync : Clock : Random](enabled: F[Boolean], config: Config): F[Tracer[F]] = {
    val tracer = for {
      enabled1 <- config.getOpt[Boolean]("enabled") if enabled1
    } yield {
      of[F](enabled)
    }
    tracer getOrElse empty[F].pure[F]
  }

  def of[F[_] : Sync : Clock : Random](enabled: F[Boolean]): F[Tracer[F]] = {
    apply[F](enabled).pure[F]
  }

  def apply[F[_] : Sync : Clock : Random](enabled: F[Boolean]): Tracer[F] = new Tracer[F] {

    def spanId = {
      for {
        enabled <- enabled
        spanId  <- if (!enabled) none[SpanId].pure[F] else {
          for {
            long <- Random[F].long
          } yield {
            SpanId(long).some
          }
        }
      } yield spanId
    }

    def trace(parentId: Option[SpanId]) = {
      for {
        enabled <- enabled
        trace   <- if (!enabled) none[Trace].pure[F] else {
          for {
            timestamp <- Clock[F].instant
            long      <- Random[F].long
            int       <- Random[F].int
          } yield {
            val spanId = SpanId(long)
            val traceId = TraceId(timestamp, int, long)
            Trace(traceId, spanId, parentId).some
          }
        }
      } yield trace
    }
  }
}
