# Stracer [![Build Status](https://travis-ci.org/evolution-gaming/stracer.svg)](https://travis-ci.org/evolution-gaming/stracer) [![Coverage Status](https://coveralls.io/repos/evolution-gaming/stracer/badge.svg)](https://coveralls.io/r/evolution-gaming/stracer) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/4b3e6d6ca1224b1bb7004b18760f7fa8)](https://www.codacy.com/app/evolution-gaming/stracer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=evolution-gaming/stracer&amp;utm_campaign=Badge_Grade) [ ![version](https://api.bintray.com/packages/evolutiongaming/maven/stracer/images/download.svg) ](https://bintray.com/evolutiongaming/maven/stracer/_latestVersion) [![License: MIT](https://img.shields.io/badge/License-MIT-yellowgreen.svg)](https://opensource.org/licenses/MIT)

Library for distributed tracing in Scala

## Span

```scala
final case class Span(
  traceId: TraceId,
  spanId: SpanId,
  parentId: Option[SpanId] = None,
  kind: Option[Span.Kind] = None,
  name: Option[String] = None,
  timestamp: Option[Instant] = None,
  duration: Option[FiniteDuration] = None,
  localEndpoint: Option[Endpoint] = None,
  remoteEndpoint: Option[Endpoint] = None,
  annotations: List[Annotation] = List.empty,
  tags: Map[String, String] = Map.empty,
  debug: Option[Boolean] = None,
  shared: Option[Boolean] = None)
```

## Trace

```scala
final case class Trace(
  traceId: TraceId,
  spanId: SpanId,
  parentId: Option[SpanId] = None,
  sampling: Option[Sampling] = None)
``` 
 
## Tracer

Generate SpanId and Trace 

```scala
trait Tracer[F[_]] {

  def spanId: F[SpanId]

  def trace(parentId: Option[SpanId] = None): F[Trace]
}
```


## ReportSpan

We provide [Kafka](http://kafka.apache.org) based implementation, which uses `SpanBytesEncoder.THRIFT` from [Zipkin](http://zipkin.io/) to encode spans.
It is also compatible with [Jaeger](https://www.jaegertracing.io) & [Ingester](https://www.jaegertracing.io/docs/1.8/deployment/#ingester) 

```scala
trait ReportSpan[F[_]] {

  def apply(span: Span): F[Unit]
}
```
 

## Setup

```scala
resolvers += Resolver.bintrayRepo("evolutiongaming", "maven")

libraryDependencies += "com.evolutiongaming" %% "stracer" % "0.0.1"
```