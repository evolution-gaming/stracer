package com.evolutiongaming.stracer

import cats.effect._
import cats.implicits._
import com.evolutiongaming.random.{Random, ThreadLocalRandom}
import com.evolutiongaming.stracer.IOSuite._
import org.scalatest.{AsyncFunSuite, Matchers}

class TracerSpec extends AsyncFunSuite with Matchers {

  private val tests = for {
    random <- ThreadLocalRandom.of[IO]
  } yield {

    implicit val random1 = random

    test("return random traces") {
      randomTraces[IO].run()
    }

    test("return none when disabled") {
      returnNone[IO].run()
    }
  }

  tests.unsafeRunSync()

  private def randomTraces[F[_]: Sync: Clock: Random]: F[Unit] =
    for {
      tracer <- Tracer.of[F](true.pure[F])
      trace1 <- tracer.trace()
      trace2 <- tracer.trace()
    } yield {
      trace1 shouldBe a[Some[_]]
      trace2 shouldBe a[Some[_]]
      val _ = for {
        trace1 <- trace1
        trace2 <- trace2
      } yield {
        trace1 should not equal trace2
        trace1.traceId should not equal trace2.traceId
        trace1.spanId should not equal trace2.spanId
      }
    }

  private def returnNone[F[_]: Sync: Clock: Random]: F[Unit] =
    for {
      tracer <- Tracer.of[F](false.pure[F])
      trace  <- tracer.trace()
    } yield {
      trace shouldEqual none
      ()
    }
}
