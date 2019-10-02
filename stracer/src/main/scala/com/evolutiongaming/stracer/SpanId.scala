package com.evolutiongaming.stracer

import com.evolutiongaming.stracer.hex.implicits._
import com.evolutiongaming.stracer.hex.{FromHex, Hex, ToHex}

sealed abstract case class SpanId(hex: String)

object SpanId {

  implicit val SpanIdToHex: ToHex[SpanId] = ToHex[Hex].imap(_.hex)

  implicit val SpanIdFromHex: FromHex[SpanId] = FromHex[Long].map(apply)

  def apply(value: Long): SpanId = {

    def apply(value: Long): SpanId = {
      val hex = value.toHex
      new SpanId(hex) {}
    }

    apply(if (value == 0) 1 else value)
  }
}
