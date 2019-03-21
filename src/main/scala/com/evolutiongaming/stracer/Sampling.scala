package com.evolutiongaming.stracer

/**
  * See [[https://github.com/openzipkin/b3-propagation#sampling-state]]
  */
sealed abstract class Sampling extends Product

object Sampling {

  val Values: Set[Sampling] = Set(Deny, Accept, Debug)

  case object Deny extends Sampling
  case object Accept extends Sampling
  case object Debug extends Sampling
}