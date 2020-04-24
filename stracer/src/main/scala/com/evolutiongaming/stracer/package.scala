package com.evolutiongaming

package object stracer {

  type Tags = List[Tag]

  type TraceOf[A] = A => TraceCtx

  type TraceCtx = Option[Trace]
}
