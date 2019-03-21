package com.evolutiongaming.stracer.hex

import cats.implicits._
import scodec.bits.ByteVector
import scodec.{DecodeResult, Decoder, Err, codecs}

trait FromHex[A] {

  def apply(hex: Hex): Either[String, A]
}

object FromHex {

  implicit val LongFromHex: FromHex[Long] = FromHex.fromDecoder(codecs.int64)

  implicit val HexToHex: FromHex[Hex] = new FromHex[Hex] {
    def apply(a: Hex) = a.asRight
  }


  def apply[A](implicit F: FromHex[A]): FromHex[A] = F


  def fromDecoder[A](decoder: Decoder[A]): FromHex[A] = {

    val errToString = (err: Err) => err.messageWithContext.asLeft[DecodeResult[A]]

    new FromHex[A] {

      def apply(hex: Hex) = {
        for {
          bytes  <- ByteVector.fromHexDescriptive(hex)
          bits    = bytes.bits
          result <- decoder.decode(bits).fold(errToString, _.asRight)
        } yield {
          result.value
        }
      }
    }
  }


  implicit class FromHexOps[A](val self: FromHex[A]) extends AnyVal {

    final def map[B](f: A => B): FromHex[B] = new FromHex[B] {

      def apply(hex: Hex) = self(hex).map(f)
    }
  }
}