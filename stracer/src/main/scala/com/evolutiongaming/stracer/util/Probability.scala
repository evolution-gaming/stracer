package com.evolutiongaming.stracer.util

import com.evolutiongaming.stracer.Sampling
import com.evolutiongaming.stracer.Sampling._

trait Probability {
  def apply(sampling: Option[Sampling]): Double
}

object Probability {

  val defaultProbabilityValue = 0.01
  val alwaysProbabilityValue = 1.0
  val neverProbabilityValue = -1.0


  val default = new Probability {
    override def apply(sampling: Option[Sampling]): Double = sampling match {
      case Some(Accept) => alwaysProbabilityValue
      case Some(Debug) => alwaysProbabilityValue
      case Some(Deny) => defaultProbabilityValue
      case None => defaultProbabilityValue
    }
  }

  val always = new ConstProbability(alwaysProbabilityValue)

  val never = new ConstProbability(neverProbabilityValue)

  class ConstProbability(value: Double) extends Probability {
    override def apply(sampling: Option[Sampling]): Double = value
  }
}
