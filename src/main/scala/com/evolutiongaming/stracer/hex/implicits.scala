package com.evolutiongaming.stracer.hex

object implicits {

  implicit class ToHexIdOps[A](val self: A) extends AnyVal {

    def toHex(implicit toHex: ToHex[A]): Hex = toHex(self)
  }


  implicit class HexOps(val self: Hex) extends AnyVal {

    def fromHex[A](implicit fromHex: FromHex[A]): Either[String, A] = fromHex(self)
  }
}
