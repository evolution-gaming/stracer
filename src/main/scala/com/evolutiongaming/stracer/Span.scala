package com.evolutiongaming.stracer

import java.time.Instant

import scala.concurrent.duration.FiniteDuration

final case class Span(
  traceId: TraceId,
  spanId: SpanId,
  name: String,
  timestamp: Instant,
  kind: Option[Kind] = None,
  duration: Option[FiniteDuration] = None,
  remoteEndpoint: Option[Endpoint] = None,
  tags: Tags = List.empty,
  shared: Option[Boolean] = None
)
