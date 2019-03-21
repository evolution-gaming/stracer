package com.evolutiongaming.stracer

import zipkin2.{Endpoint => EndpointJ}

final case class Endpoint(
  serviceName: Option[String] = None,
  ip: Option[String] = None,
  port: Option[Int] = None)

object Endpoint {

  val Empty: Endpoint = Endpoint()


  implicit class EndpointOps(val self: Endpoint) extends AnyVal {

    def toJava: EndpointJ = {
      EndpointJ.newBuilder()
        .serviceName(self.serviceName)
        .ip(self.ip)
        .port(self.port)
        .build()
    }
  }


  implicit class EndpointBuilderOps(val self: EndpointJ.Builder) extends AnyVal {

    def serviceName(a: Option[String]): EndpointJ.Builder = a.fold(self)(self.serviceName)

    def ip(a: Option[String]): EndpointJ.Builder = a.fold(self)(self.ip)

    def port(a: Option[Int]): EndpointJ.Builder = a.fold(self)(self.port)
  }
}