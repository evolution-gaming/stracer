package com.evolutiongaming.random

import cats.effect.{Clock, IO, Sync}
import cats.implicits._
import com.evolutiongaming.catshelper.ThreadLocalOf
import com.evolutiongaming.stracer.IOSuite._
import org.scalatest.{AsyncFunSuite, Matchers}

class ThreadLocalRandomSpec extends AsyncFunSuite with Matchers {

  test("return random numbers") {
    randomTraces[IO].run()
  }

  private def randomTraces[F[_] : Sync : Clock : ThreadLocalOf]: F[Unit] = {
    for {
      random <- ThreadLocalRandom.of[F]
      n1 <- random.int
      n2 <- random.int
    } yield {
      n1 should not equal n2
      ()
    }
  }
}