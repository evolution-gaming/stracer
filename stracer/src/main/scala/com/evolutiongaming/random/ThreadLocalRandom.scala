package com.evolutiongaming.random

import cats.FlatMap
import cats.effect.{Clock, Sync}
import cats.syntax.all._
import com.evolutiongaming.catshelper.{ThreadLocalOf, ThreadLocalRef}

object ThreadLocalRandom {

  def of[F[_]: FlatMap: Clock: ThreadLocalOf]: F[Random[F]] = {
    val random = Random.State.fromClock[F]()
    for {
      random <- ThreadLocalOf[F].apply(random)
    } yield {
      apply(random)
    }

  }

  def sync[F[_]: Sync: Clock: ThreadLocalOf]: F[Random[F]] =
    for {
      randomOf <- RandomStateOf.of[F]
      randomInstance <- randomOf()
      random <- ThreadLocalOf[F].apply(randomInstance)
    } yield {
      apply(random)
    }

  def apply[F[_]](random: ThreadLocalRef[F, Random.State]): Random[F] = new Random[F] {

    def int = random.modify(_.int)

    def long = random.modify(_.long)

    def float = random.modify(_.float)

    def double = random.modify(_.double)
  }
}
