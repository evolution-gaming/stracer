package com.evolutiongaming.stracer

import cats.data.{NonEmptyList => Nel}
import com.evolutiongaming.skafka.{ToBytes, Topic}
import zipkin2.codec.SpanBytesEncoder

import scala.collection.JavaConverters._

object ToBytesOf {

  def apply(encode: SpanBytesEncoder): ToBytes[Nel[SpanRecord]] = new ToBytes[Nel[SpanRecord]] {

    def apply(spans: Nel[SpanRecord], topic: Topic) = {
      val spansJ = spans.toList.map(_.toJava).asJava
      encode.encodeList(spansJ)
    }
  }
}
