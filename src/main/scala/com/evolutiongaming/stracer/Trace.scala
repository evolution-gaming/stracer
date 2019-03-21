package com.evolutiongaming.stracer

final case class Trace(
  traceId: TraceId,
  spanId: SpanId,
  parentId: Option[SpanId] = None,
  sampling: Option[Sampling] = None)
