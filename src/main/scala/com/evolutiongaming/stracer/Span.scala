package com.evolutiongaming.stracer

import java.time.Instant
import com.evolutiongaming.stracer.TracingHelper._

import zipkin2.{Span => SpanJ}

import scala.concurrent.duration.FiniteDuration

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

object Span {

  sealed abstract class Kind extends Product

  object Kind {

    case object Client extends Kind
    case object Server extends Kind
    case object Producer extends Kind
    case object Consumer extends Kind


    implicit class KindOps(val self: Kind) extends AnyVal {

      def toJava: SpanJ.Kind = self match {
        case Client   => SpanJ.Kind.CLIENT
        case Server   => SpanJ.Kind.SERVER
        case Producer => SpanJ.Kind.PRODUCER
        case Consumer => SpanJ.Kind.CONSUMER
      }
    }
  }


  implicit class SpanOps(val self: Span) extends AnyVal {

    def toJava: SpanJ = {
      SpanJ.newBuilder()
        .traceId(self.traceId.hex)
        .parentId(self.parentId)
        .id(self.spanId.hex)
        .kind(self.kind)
        .name(self.name)
        .timestamp(self.timestamp)
        .duration(self.duration)
        .localEndpoint(self.localEndpoint)
        .remoteEndpoint(self.remoteEndpoint)
        .annotations(self.annotations)
        .tags(self.tags)
        .debug(self.debug)
        .shared(self.shared)
        .build()
    }
  }


  implicit class SpanBuilderOps(val self: SpanJ.Builder) extends AnyVal {

    def parentId(a: Option[SpanId]): SpanJ.Builder = a.fold(self)(a => self.parentId(a.hex))

    def kind(a: Option[Kind]): SpanJ.Builder = a.fold(self)(a => self.kind(a.toJava))

    def name(a: Option[String]): SpanJ.Builder = a.fold(self)(self.name)

    def timestamp(a: Option[Instant]): SpanJ.Builder = a.fold(self)(a => self.timestamp(a.micros))

    def duration(a: Option[FiniteDuration]): SpanJ.Builder = a.fold(self)(a => self.duration(a.toMicros))

    def localEndpoint(a: Option[Endpoint]): SpanJ.Builder = {
      a.fold(self) { a =>
        if (a == Endpoint.Empty) self
        else self.localEndpoint(a.toJava)
      }
    }

    def remoteEndpoint(a: Option[Endpoint]): SpanJ.Builder = {
      a.fold(self) { a =>
        if (a == Endpoint.Empty) self
        else self.remoteEndpoint(a.toJava)
      }
    }

    def annotations(a: List[Annotation]): SpanJ.Builder = {
      if (a.isEmpty) self
      else a.foldLeft(self) { (self, a) => self.addAnnotation(a.timestamp.micros, a.value) }
    }

    def tags(a: Map[String, String]): SpanJ.Builder = {
      if (a.isEmpty) self
      else a.foldLeft(self) { case (self, (key, value)) => self.putTag(key, value) }
    }

    def debug(a: Option[Boolean]): SpanJ.Builder = a.fold(self)(self.debug)

    def shared(a: Option[Boolean]): SpanJ.Builder = a.fold(self)(self.shared)
  }
}