package com.evolutiongaming.stracer

import io.circe.Decoder.Result
import io.circe.{Codec, DecodingFailure, HCursor, Json}
import io.circe.generic.semiauto._
import cats.syntax.either._
import com.evolutiongaming.stracer.hex.{FromHex, ToHex}

object TraceJson {

  implicit val samplingCodec: Codec[Sampling] = new Codec[Sampling] {
    def name(a: Sampling): String = a.toString.toLowerCase
    val elements                  = Sampling.Values.map(a => (name(a), a)).toMap

    override def apply(a: Sampling): Json = Json.fromString(name(a))

    override def apply(c: HCursor): Result[Sampling] =
      for {
        str <- c.as[String]
        sampling <- elements
          .get(str)
          .toRight(DecodingFailure(s"Value $str is not a recognized sampling type.", List.empty))
      } yield sampling
  }

  def hexCodec[A](implicit toHex: ToHex[A], fromHex: FromHex[A]): Codec[A] = new Codec[A] {
    override def apply(a:  A): Json = Json.fromString(toHex(a))
    override def apply(c: HCursor): Result[A] =
      for {
        hex   <- c.as[String]
        value <- fromHex(hex).leftMap(
          error => DecodingFailure(s"Value $hex is not a proper hex. Got error $error while decoding.", List.empty)
        )
      } yield value
  }

  implicit val traceIdCodec: Codec[TraceId] = hexCodec[TraceId]

  implicit val spanIdCodec: Codec[SpanId] = hexCodec[SpanId]

  implicit val traceCodec: Codec[Trace] = deriveCodec[Trace]

}
