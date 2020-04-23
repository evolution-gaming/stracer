package com.evolutiongaming.stracer

import cats.implicits._
import cats.Monad
import cats.effect.Clock
import com.evolutiongaming.random.Random

import com.evolutiongaming.catshelper.ClockHelper._

trait TraceGen[F[_]] {

  def root: F[Trace]

  def childOf(trace: Trace): F[Trace]
}

object TraceGen {

  def summon[F[_]](implicit ev: TraceGen[F]): TraceGen[F] = ev

  def apply[F[_]: Monad: Clock: Random](): TraceGen[F] =
    new TraceGen[F] {

      private val newSpanId = Random[F].long.map(SpanId(_))

      def root: F[Trace] =
        for {
          timestamp <- Clock[F].instant
          long      <- Random[F].long
          int       <- Random[F].int
        } yield {
          val spanId  = SpanId(long)
          val traceId = TraceId(timestamp, int, long)
          Trace(traceId, spanId, none, timestamp.some, none)
        }

      def childOf(trace:  Trace): F[Trace] =
        for {
          timestamp <- Clock[F].instant
          spanId    <- newSpanId
        } yield {
          Trace(trace.traceId, spanId, trace.spanId.some, timestamp.some, trace.sampling)
        }
  }
}