package com.evolutiongaming

package object stracer {

  type Tags = List[Tag]

  @deprecated("Please use Option[Trace] directly so it's less confusing", "29/06/2021")
  type TraceCtx = Option[Trace]
}
