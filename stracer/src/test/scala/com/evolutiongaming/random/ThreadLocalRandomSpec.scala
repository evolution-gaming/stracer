package com.evolutiongaming.random

import cats.effect.IO
import com.evolutiongaming.stracer.IOSuite._
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

class ThreadLocalRandomSpec extends AsyncFunSuite with Matchers {

  test("return random numbers") {
    randomTraces.run()
  }

  private def randomTraces: IO[Unit] =
    for {
      random <- ThreadLocalRandom.of[IO]
      n1     <- random.int
      n2     <- random.int
    } yield {
      n1 should not equal n2
      ()
    }
}
