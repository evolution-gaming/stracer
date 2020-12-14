package com.evolutiongaming.stracer

import cats.syntax.all._
import java.time.Instant
import com.evolutiongaming.stracer.TraceJson.TraceFormat
import play.api.libs.json.{JsSuccess, Json}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TraceJsonSpec extends AnyFunSuite with Matchers {

  test("toJson & fromJson") {

    val expected = Json.parse(
      getClass.getResourceAsStream("trace.json")
    )
    val timestamp = Instant.ofEpochMilli(1551824013554L)
    val traceId   = TraceId(timestamp, randomInt = 12345, randomLong = 123456789L)
    val spanId    = SpanId(1551818273913L)
    val trace = Trace(
      traceId = traceId,
      spanId = spanId,
      parentId = none,
      timestamp = timestamp.some,
      sampling = Sampling.Accept.some
    )

    val json = Json.toJson(trace)
    json shouldEqual expected
    json.validate[Trace] shouldEqual JsSuccess(trace)
  }
}
