package com.evolutiongaming.stracer

import com.evolutiongaming.stracer.hex.{FromHex, ToHex}
import com.evolutiongaming.util.JsonFormats.ObjectFormat
import play.api.libs.json._

object TraceJson {

  implicit val SamplingFormat: Format[Sampling] = {
    def name(a: Sampling) = a.toString.toLowerCase

    val map = Sampling.Values.map(a => (name(a), a)).toMap
    ObjectFormat(map.get, name)
  }

  implicit val TraceIdFormat: Format[TraceId] = formatOf[TraceId]

  implicit val SpanIdFormat: Format[SpanId] = formatOf[SpanId]

  implicit val TraceFormat: OFormat[Trace] = Json.format[Trace]

  private def formatOf[A](implicit toHex: ToHex[A], fromHex: FromHex[A]): Format[A] = new Format[A] {

    def writes(x: A): JsValue = JsString(toHex(x))

    def reads(json: JsValue): JsResult[A] =
      for {
        s <- json.validate[String]
        a <- fromHex(s).jsResult
      } yield a
  }

  implicit class EitherOps[A](val self: Either[String, A]) extends AnyVal {

    def jsResult: JsResult[A] = self match {
      case Right(a) => JsSuccess(a)
      case Left(a)  => JsError(a)
    }
  }
}
