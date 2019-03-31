package com.evolutiongaming

package object stracer {

  type Tags = List[Tag]

  type TagsOf[A] = A => Tags

  type TraceOf[A] = A => Option[Trace]
}
