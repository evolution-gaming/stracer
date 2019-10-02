package com.evolutiongaming.stracer

import java.time.Instant
import java.util.concurrent.TimeUnit

object TracingHelper {

  implicit class InstantOps(val self: Instant) extends AnyVal {

    def micros: Long = {
      val seconds = TimeUnit.SECONDS.toMicros(self.getEpochSecond)
      val nanos   = TimeUnit.NANOSECONDS.toMicros(self.getNano.toLong)
      seconds + nanos
    }
  }

  implicit class EitherStringOps[A](val self: Either[String, A]) extends AnyVal {

    def unsafe: A = self match {
      case Right(a) => a
      case Left(a)  => throw new IllegalArgumentException(a)
    }
  }
}
