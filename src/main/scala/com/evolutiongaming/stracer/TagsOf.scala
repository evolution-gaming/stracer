package com.evolutiongaming.stracer

import com.evolutiongaming.stracer.implicits._

trait TagsOf[A] {

  def apply(a: A): Tags
}

object TagsOf {

  implicit val tagsOfTag: TagsOf[Tag] = (a: Tag) => Tags(a)

  implicit val tagsOfTags: TagsOf[Tags] = (a: Tags) => a

  implicit val tagsOfTuple: TagsOf[(String, String)] = { case (name, value) => Tags(Tag(name, value)) }

  implicit def tagsOfOption[A: TagsOf]: TagsOf[Option[A]] = (a: Option[A]) => a.fold(Tags.Empty)(_.tags)

  implicit def tagsOfEither[A: TagsOf, B: TagsOf]: TagsOf[Either[A, B]] = _.fold(_.tags, _.tags)
}
