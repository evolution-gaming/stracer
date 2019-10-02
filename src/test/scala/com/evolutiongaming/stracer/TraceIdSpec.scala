package com.evolutiongaming.stracer

import java.time.Instant

import cats.implicits._
import org.scalatest.{FunSuite, Matchers}

class TraceIdSpec extends FunSuite with Matchers {

  private val timestamp = Instant.ofEpochMilli(1551824013554L)
  private val traceId   = TraceId(timestamp, randomInt = 12345, randomLong = 123456789L)
  private val hex       = "5c7ef48d0000303900000000075bcd15"

  test("apply") {
    val str = traceId.hex
    str shouldEqual hex
  }

  test("fromHex") {
    TraceId.fromHex(hex) shouldEqual traceId.asRight
  }

  test("toString") {
    traceId.toString shouldEqual "TraceId(5c7ef48d0000303900000000075bcd15)"
  }
}
