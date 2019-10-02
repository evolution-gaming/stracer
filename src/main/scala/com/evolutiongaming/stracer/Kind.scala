package com.evolutiongaming.stracer

import zipkin2.{Span => SpanJ}

sealed abstract class Kind extends Product

object Kind {

  def client: Kind = Client

  def server: Kind = Server

  def producer: Kind = Producer

  def consumer: Kind = Consumer

  case object Client extends Kind

  case object Server extends Kind

  case object Producer extends Kind

  case object Consumer extends Kind

  implicit class KindOps(val self: Kind) extends AnyVal {

    def toJava: SpanJ.Kind = self match {
      case Client   => SpanJ.Kind.CLIENT
      case Server   => SpanJ.Kind.SERVER
      case Producer => SpanJ.Kind.PRODUCER
      case Consumer => SpanJ.Kind.CONSUMER
    }
  }
}
