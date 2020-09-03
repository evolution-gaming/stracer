package com.evolutiongaming.stracer

import java.time.Instant

import cats.syntax.all._
import com.evolutiongaming.stracer.TracingHelper._
import zipkin2.{Endpoint => EndpointJ, Span => SpanJ}
import cats.syntax.all._

import scala.concurrent.duration._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SpanRecordSpec extends AnyFunSuite with Matchers {

  test("toJava") {

    val timestamp = Instant.ofEpochMilli(1551824013554L)

    val traceId = TraceId(timestamp, randomInt = 2, randomLong = 1551818273913L)

    val spanId: SpanId = SpanId(1551818273913L)

    val span = SpanRecord(
      traceId = traceId,
      parentId = spanId.some,
      spanId = spanId,
      kind = Kind.Server.some,
      name = "name".some,
      timestamp = timestamp.some,
      duration = 1.millis.some,
      localEndpoint = Endpoint(
        serviceName = "local".some,
        ip = "0.0.0.0".some,
        port = 0.some
      ).some,
      remoteEndpoint = Endpoint(
        serviceName = "remote".some,
        ip = "1.1.1.1".some,
        port = 1.some
      ).some,
      annotations = List(
        Annotation("1", timestamp),
        Annotation("2", timestamp)
      ),
      tags = Map(
        ("key-1", "val-1"),
        ("key-2", "val-2")
      ),
      debug = true.some,
      shared = false.some
    )

    val expected = SpanJ
      .newBuilder()
      .traceId("5c7ef48d00000002000001694f93b479")
      .parentId("000001694f93b479")
      .id("000001694f93b479")
      .kind(SpanJ.Kind.SERVER)
      .name("name")
      .timestamp(timestamp.micros)
      .duration(1000)
      .localEndpoint(
        EndpointJ
          .newBuilder()
          .serviceName("local")
          .ip("0.0.0.0")
          .port(0)
          .build()
      )
      .remoteEndpoint(
        EndpointJ
          .newBuilder()
          .serviceName("remote")
          .ip("1.1.1.1")
          .port(1)
          .build()
      )
      .addAnnotation(timestamp.micros, "1")
      .addAnnotation(timestamp.micros, "2")
      .putTag("key-1", "val-1")
      .putTag("key-2", "val-2")
      .debug(true)
      .shared(false)
      .build()

    span.toJava shouldEqual expected
  }
}
