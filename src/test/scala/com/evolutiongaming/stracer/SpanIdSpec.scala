package com.evolutiongaming.stracer

import org.scalatest.{FunSuite, Matchers}

class SpanIdSpec extends FunSuite with Matchers {

  private val spanId = SpanId(1551818273913L)

  test("apply") {
    SpanId(0) shouldEqual SpanId(1)
    SpanId(1) shouldEqual SpanId(1)
  }

  test("toString") {
    spanId.toString shouldEqual "SpanId(000001694f93b479)"
  }
}
