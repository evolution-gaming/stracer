package com.evolutiongaming.stracer

import io.circe.Decoder.Result
import io.circe.{Codec, Decoder, DecodingFailure, Encoder, HCursor, Json}
import io.circe.generic.semiauto._
import cats.syntax.either._

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

  implicit val traceIdEncoder: Encoder[TraceId] = Encoder.encodeString.contramap(_.hex)
  implicit val traceIdDecoder: Decoder[TraceId] = new Decoder[TraceId] {
    override def apply(c: HCursor): Result[TraceId] =
      for {
        hex <- c.as[String]
        traceId <- TraceId
          .fromHex(hex)
          .leftMap(error => DecodingFailure(s"Value $hex is not a proper hex. Got error $error while decoding.", List.empty))
      } yield traceId
  }

  implicit val spanIdEncoder: Encoder[SpanId] = Encoder.encodeString.contramap(_.hex)
  implicit val spanIdDecoder: Decoder[SpanId] = Decoder.decodeLong.map(SpanId.apply)

  implicit val traceCodec: Codec[Trace] = deriveCodec[Trace]

}
