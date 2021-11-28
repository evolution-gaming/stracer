package com.evolutiongaming.stracer

import cats.syntax.all._

trait TraceOf[-A] {
  def apply(a: A): Option[Trace]
}

object TraceOf {

  def apply[A](implicit F: TraceOf[A]): TraceOf[A] = F

  def const[A](trace: Option[Trace]): TraceOf[A] = {
    class Const
    new Const with TraceOf[A] {
      def apply(a: A) = trace
    }
  }

  implicit val traceTraceOf: TraceOf[Trace] = new TraceOf[Trace] {
    def apply(a: Trace) = a.some
  }

  object implicits {
    implicit class IdOpsTraceOf[A](val self: A) extends AnyVal {
      def trace(implicit traceOf: TraceOf[A]): Option[Trace] = traceOf(self)
    }
  }
}