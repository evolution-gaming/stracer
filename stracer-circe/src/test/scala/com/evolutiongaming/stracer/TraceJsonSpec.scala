package com.evolutiongaming.stracer

import java.time.Instant
import cats.implicits._
import com.evolutiongaming.stracer.TraceJson._
import org.scalatest.{FunSuite, Matchers}
import io.circe.parser._
import scala.io.Source
import io.circe.syntax._

class TraceJsonSpec extends FunSuite with Matchers {

  test("toJson & fromJson") {

    val resourceStream = getClass.getResourceAsStream("trace.json")
    val expected       = parse(Source.fromInputStream(resourceStream).mkString).right.get
    val timestamp      = Instant.ofEpochMilli(1551824013554L)
    val traceId        = TraceId(timestamp, randomInt = 12345, randomLong = 123456789L)
    val spanId         = SpanId(1551818273913L)
    val trace = Trace(
      traceId = traceId,
      spanId = spanId,
      timestamp = timestamp.some,
      sampling = Sampling.Accept.some
    )

    val json = trace.asJson
    json shouldEqual expected
  }
}
