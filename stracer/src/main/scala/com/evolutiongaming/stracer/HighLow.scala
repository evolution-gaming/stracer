package com.evolutiongaming.stracer

import com.evolutiongaming.stracer.hex.{FromHex, ToHex}
import scodec.*
import scodec.codecs.*

final case class HighLow(high: Long, low: Long)

object HighLow {

  private val HighLowCodec = (int64 :: int64).as[HighLow]

  implicit val HighLowToHex: ToHex[HighLow] = ToHex.fromEncoder(HighLowCodec)

  implicit val HighLowFromHex: FromHex[HighLow] = FromHex.fromDecoder(HighLowCodec)
}
