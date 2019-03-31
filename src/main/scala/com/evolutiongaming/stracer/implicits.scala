package com.evolutiongaming.stracer


object implicits {

  implicit val tagsOfTag: TagsOf[Tag] = (a: Tag) => Tags(a)

  implicit val tagsOfTuple: TagsOf[(String, String)] = { case (name, value) => Tags(Tag(name, value)) }

  implicit def tagsOfOption[A: TagsOf]: TagsOf[Option[A]] = (a: Option[A]) => a.fold(Tags.Empty)(_.tags)

  implicit def tagsOfEither[A: TagsOf, B: TagsOf]: TagsOf[Either[A, B]] = _.fold(_.tags, _.tags)


  implicit class TagsIdOps[A](val a: A) extends AnyVal {

    def tags(implicit F: TagsOf[A]): Tags = F(a)

    def &[B](b: B)(implicit FA: TagsOf[A], FB: TagsOf[B]): Tags = FB(b) ++ FA(a)
  }
}
