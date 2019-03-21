package com.evolutiongaming.stracer

import com.evolutiongaming.stracer.hex.{FromHex, ToHex}
import scodec.codecs

final case class HighLow(high: Long, low: Long)

object HighLow {

  private val HighLowCodec = {
    val codec = codecs.int64 ~ codecs.int64

    val to = (a: (Long, Long)) => {
      val (high, low) = a
      HighLow(high = high, low = low)
    }
    codec.xmap[HighLow](to, a => (a.high, a.low))
  }

  implicit val HighLowToHex: ToHex[HighLow] = ToHex.fromEncoder(HighLowCodec)

  implicit val HighLowFromHex: FromHex[HighLow] = FromHex.fromDecoder(HighLowCodec)
}
