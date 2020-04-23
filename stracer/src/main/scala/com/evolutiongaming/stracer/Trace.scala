package com.evolutiongaming.stracer

import java.time.Instant

final case class Trace(
  traceId: TraceId,
  spanId: SpanId,
  parentId: Option[SpanId],
  timestamp: Option[Instant],
  sampling: Option[Sampling] = None
)

object Trace {

  implicit class TraceOps(val self: Trace) extends AnyVal {

    def child[F[_]](implicit traceGen: TraceGen[F]): F[Trace] = traceGen.childOf(self)
  }
}