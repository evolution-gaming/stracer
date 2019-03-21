package com.evolutiongaming.stracer

import java.time.Instant

import com.evolutiongaming.stracer.hex.implicits._
import com.evolutiongaming.stracer.hex.{FromHex, Hex, ToHex}

abstract sealed case class TraceId(hex: Hex)

object TraceId {

  implicit val TraceIdToHex: ToHex[TraceId] = ToHex[Hex].imap(_.hex)

  implicit val TraceIdFromHex: FromHex[TraceId] = FromHex[HighLow].map(apply)


  def apply(timestamp: Instant, randomInt: Int, randomLong: Long): TraceId = {
    val epochSeconds = timestamp.toEpochMilli / 1000
    val high = (epochSeconds & 0xffffffffL) << 32 | (randomInt & 0xffffffffL)
    apply(HighLow(high = high, low = randomLong))
  }

  private def apply(highLow: HighLow): TraceId = {
    val hex = highLow.toHex
    new TraceId(hex) {}
  }

  def fromHex(hex: Hex): Either[String, TraceId] = {
    for {
      _ <- hex.fromHex[HighLow]
    } yield {
      new TraceId(hex) {}
    }
  }
}