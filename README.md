# STracer
[![Build Status](https://github.com/evolution-gaming/stracer/workflows/CI/badge.svg)](https://github.com/evolution-gaming/stracer/actions?query=workflow:CI+branch:master)
[![Coverage Status](https://coveralls.io/repos/github/evolution-gaming/stracer/badge.svg?branch=master)](https://coveralls.io/github/evolution-gaming/stracer?branch=master)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/02ff04792f8745e89ba4a5fe34794a77)](https://app.codacy.com/gh/evolution-gaming/stracer/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Version](https://img.shields.io/badge/version-click-blue)](https://evolution.jfrog.io/artifactory/api/search/latestVersion?g=com.evolutiongaming&a=stracer_2.13&repos=public)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellowgreen.svg)](https://opensource.org/licenses/MIT)

Library for distributed tracing in Scala

## Span

```scala
final case class Span(
  traceId: TraceId,
  spanId: SpanId,
  name: String,
  timestamp: Instant,
  kind: Option[Kind] = None,
  duration: Option[FiniteDuration] = None,
  remoteEndpoint: Option[Endpoint] = None,
  tags: Tags = List.empty,
  shared: Option[Boolean] = None,
  parentId: Option[SpanId])
```

## Trace

```scala
final case class Trace(
  traceId: TraceId,
  spanId: SpanId,
  parentId: Option[SpanId],
  timestamp: Option[Instant],
  sampling: Option[Sampling] = None)
``` 
 
## Tracer

Generate SpanId and Trace 

```scala
trait Tracer[F[_]] {

  def trace(sampling: Option[Sampling] = None): F[Option[Trace]]
}
```


## ReportSpan

We provide [Kafka](http://kafka.apache.org) based implementation, which uses `SpanBytesEncoder.THRIFT` from [Zipkin](http://zipkin.io/) to encode spans.
It is also compatible with [Jaeger](https://www.jaegertracing.io) & [Ingester](https://www.jaegertracing.io/docs/1.8/deployment/#ingester)  
Since version `5.0.0` this implementation is available in separate module `stracer-kafka`.

```scala
trait ReportSpan[F[_]] {

  def apply(span: Span): F[Unit]
}
```
 

## Setup

```scala
addSbtPlugin("com.evolution" % "sbt-artifactory-plugin" % "0.0.2")

libraryDependencies += "com.evolutiongaming" %% "stracer"           % "5.0.0"
libraryDependencies += "com.evolutiongaming" %% "stracer-play-json" % "5.0.0"
libraryDependencies += "com.evolutiongaming" %% "stracer-circe"     % "5.0.0"
libraryDependencies += "com.evolutiongaming" %% "stracer-kafka"     % "5.0.0"
```
