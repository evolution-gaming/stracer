package com.evolutiongaming.stracer

import cats.syntax.all._
import java.time.Instant
import com.evolutiongaming.stracer.TraceJson._
import io.circe.Decoder
import io.circe.parser._

import scala.io.Source
import io.circe.syntax._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TraceJsonSpec extends AnyFunSuite with Matchers {

  val trace: Trace = {

    val timestamp      = Instant.ofEpochMilli(1551824013554L)
    val traceId        = TraceId(timestamp, randomInt = 12345, randomLong = 123456789L)
    val spanId         = SpanId(1551818273913L)

    Trace(
      traceId = traceId,
      spanId = spanId,
      parentId = none,
      timestamp = timestamp.some,
      sampling = Sampling.Accept.some
    )
  }

  test("toJson & fromJson") {

    val resourceStream = getClass.getResourceAsStream("trace.json")
    val expectedJson   = parse(Source.fromInputStream(resourceStream).mkString).getOrElse(sys.error("No file found."))

    val json = trace.asJson
    json shouldEqual expectedJson
    Decoder[Trace].decodeJson(expectedJson) shouldEqual Right(trace)
    Decoder[Trace].decodeJson(expectedJson.dropNullValues) shouldEqual Right(trace)
  }
}
