package com.evolutiongaming.stracer

import cats.data.{NonEmptyList => Nel}
import cats.effect.Sync
import com.evolutiongaming.skafka.{ToBytes, Topic}
import zipkin2.codec.SpanBytesEncoder

import scala.collection.JavaConverters._

object ToBytesOf {

  def apply[F[_] : Sync](encode: SpanBytesEncoder): ToBytes[F, Nel[SpanRecord]] = {
    (spans: Nel[SpanRecord], _: Topic) => {
      val spansJ = spans.toList.map(_.toJava).asJava
      Sync[F].delay { encode.encodeList(spansJ) }
    }
  }
}
