package com.evolutiongaming.stracer


object implicits {

  implicit class TagsIdOps[A](val a: A) extends AnyVal {

    def tags(implicit F: TagsOf[A]): Tags = F(a)

    def &[B](b: B)(implicit tagsOfA: TagsOf[A], tagsOfB: TagsOf[B]): Tags = tagsOfB(b) ++ tagsOfA(a)
  }
}
