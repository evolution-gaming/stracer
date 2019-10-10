package com.evolutiongaming.stracer

import cats.Monad
import cats.effect._
import cats.implicits._
import com.evolutiongaming.random.{Random, ThreadLocalRandom}
import com.evolutiongaming.stracer.IOSuite._
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers
import com.evolutiongaming.stracer.Tracer.RuntimeConf

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

  private def randomTraces[F[_]: Monad: Clock: Random] = {
    val tracer = Tracer(RuntimeConf.default.pure[F])
    for {
      trace1 <- tracer.trace()
      _      =  trace1 shouldBe a[Some[_]]
      trace2 <- tracer.trace()
      _      =  trace2 shouldBe a[Some[_]]
    } yield {
      for {
        trace1 <- trace1
        trace2 <- trace2
      } yield {
        trace1 should not equal trace2
        trace1.traceId should not equal trace2.traceId
        trace1.spanId should not equal trace2.spanId
      }
    }
  }

  private def returnNone[F[_]: Monad: Clock: Random] = {
    val tracer = Tracer(RuntimeConf(enabled = false).pure[F])
    for {
      trace  <- tracer.trace()
    } yield {
      trace shouldEqual none
    }
  }
}
