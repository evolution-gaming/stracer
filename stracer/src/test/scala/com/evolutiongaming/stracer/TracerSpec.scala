package com.evolutiongaming.stracer

import cats.Monad
import cats.effect._
import cats.implicits._
import com.evolutiongaming.random.ThreadLocalRandom
import com.evolutiongaming.stracer.IOSuite._
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers
import com.evolutiongaming.stracer.Tracer.RuntimeConf

class TracerSpec extends AsyncFunSuite with Matchers {

  private val tests = for {
    random <- ThreadLocalRandom.of[IO]
  } yield {

    implicit val random1 = random
    implicit val traceGen = TraceGen[IO]()

    test("return random traces") {
      randomTraces[IO].run()
    }

    test("return none when disabled") {
      returnNone[IO].run()
    }

    test("child traces") {
      childTraces[IO].run()
    }
  }

  tests.unsafeRunSync()

  private def randomTraces[F[_]: Monad: TraceGen] = {
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

  private def childTraces[F[_]: Monad: TraceGen] = {
    val tracer         = Tracer(RuntimeConf.default.pure[F])
    val disabledTracer = Tracer(RuntimeConf(enabled = false).pure[F])
    for {
      trace1 <- tracer.trace()
      child1 <- tracer.childOf(trace1)
      child2 <- tracer.childOf(trace1)
      child3 <- disabledTracer.childOf(trace1)
      _      =  trace1 shouldBe a[Some[_]]
      _      =  child1 shouldBe a[Some[_]]
      _      =  child1 shouldBe a[Some[_]]
      _      =  child3 shouldBe None
    } yield {
      for {
        trace1 <- trace1
        child1 <- child1
        child2 <- child2
      } yield {
        trace1 should not equal child1
        trace1 should not equal child2
        trace1.traceId shouldEqual child1.traceId
        trace1.traceId shouldEqual child2.traceId
        trace1.spanId should not equal child1.spanId
        trace1.spanId should not equal child2.spanId
        trace1.spanId.some shouldEqual child1.parentId
        trace1.spanId.some shouldEqual child2.parentId
        child1.spanId should not equal child2.spanId
      }
    }
  }

  private def returnNone[F[_]: Monad: TraceGen] = {
    val tracer = Tracer(RuntimeConf(enabled = false).pure[F])
    for {
      trace  <- tracer.trace()
    } yield {
      trace shouldEqual none
    }
  }
}
