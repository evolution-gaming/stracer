package com.evolutiongaming.stracer

import java.time.Instant

final case class Trace(
  traceId: TraceId,
  spanId: SpanId,
  timestamp: Option[Instant],
  sampling: Option[Sampling] = None
)
