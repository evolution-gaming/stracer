package com.evolutiongaming.stracer

import cats.Applicative
import cats.syntax.all._
import com.evolutiongaming.stracer.implicits._

trait ReportSpan[F[_]] {

  def apply(span: Span): F[Unit]
}

object ReportSpan {

  def empty[F[_]: Applicative]: ReportSpan[F] = const(().pure[F])

  def const[F[_]](value: F[Unit]): ReportSpan[F] = new ReportSpan[F] {
    def apply(span: Span) = value
  }

  def apply[F[_]](
    localEndpoint: Endpoint,
    tags: Tags,
    reportSpan: ReportSpanRecord[F],
    kind: Kind
  ): ReportSpan[F] = new ReportSpan[F] {

    def apply(span: Span) = {

      val tags1 = (tags & span.tags).foldRight(Map.empty[String, String]) { (tag, map) =>
        map.updated(tag.name, tag.value)
      }

      val record = SpanRecord(
        traceId = span.traceId,
        spanId = span.spanId,
        parentId = span.parentId,
        kind = span.kind orElse kind.some,
        name = span.name.some,
        timestamp = span.timestamp.some,
        duration = span.duration,
        localEndpoint = localEndpoint.some,
        remoteEndpoint = span.remoteEndpoint,
        annotations = List.empty,
        tags = tags1,
        debug = None,
        shared = span.shared
      )

      reportSpan(record)
    }
  }
}
