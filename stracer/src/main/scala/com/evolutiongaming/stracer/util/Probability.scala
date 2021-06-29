package com.evolutiongaming.stracer.util

import com.evolutiongaming.stracer.Sampling
import com.evolutiongaming.stracer.Sampling._

trait Probability {
  def apply(sampling: Option[Sampling]): Double
}

object Probability {

  private val defaultProbabilityValue = 0.01
  private val alwaysProbabilityValue = 1.0
  private val neverProbabilityValue = -1.0

  val default: Probability = {
    case Some(Accept) => alwaysProbabilityValue
    case Some(Debug) => alwaysProbabilityValue
    case Some(Deny) => defaultProbabilityValue
    case None => defaultProbabilityValue
  }

  val always: Probability = new ConstProbability(alwaysProbabilityValue)

  val never: Probability = new ConstProbability(neverProbabilityValue)

  def const(value: Double): Probability = new ConstProbability(value)

  private class ConstProbability(value: Double) extends Probability {
    override def apply(sampling: Option[Sampling]): Double = value
  }
}
