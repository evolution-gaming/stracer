package com.evolutiongaming.stracer.hex

import scodec.{Encoder, codecs}

trait ToHex[A] {

  def apply(a: A): Hex
}

object ToHex {

  implicit val LongToHex: ToHex[Long] = ToHex.fromEncoder(codecs.int64)

  implicit val HexToHex: ToHex[Hex] = new ToHex[Hex] {
    def apply(a: Hex) = a
  }


  def apply[A](implicit F: ToHex[A]): ToHex[A] = F


  def fromEncoder[A](encoder: Encoder[A]): ToHex[A] = new ToHex[A] {

    def apply(a: A) = {
      encoder.encode(a).require.bytes.toHex
    }
  }


  implicit class ToHexOps[A](val self: ToHex[A]) extends AnyVal {

    final def imap[B](f: B => A): ToHex[B] = new ToHex[B] {
      def apply(a: B) = self(f(a))
    }
  }
}