package com.evolutiongaming.stracer

import cats.Contravariant
import com.evolutiongaming.stracer.implicits._

trait TagsOf[-A] {

  def apply(a: A): Tags
}

object TagsOf {

  def empty[A]: TagsOf[A] = _ => Tags.Empty

  def apply[A](f: A => Tags): TagsOf[A] = a => f(a)


  implicit val contravariantTagsOf: Contravariant[TagsOf] = new Contravariant[TagsOf] {
    def contramap[A, B](fa: TagsOf[A])(f: B => A) = b => fa(f(b))
  }

  implicit val tagsOfTag: TagsOf[Tag] = (a: Tag) => Tags(a)

  implicit val tagsOfTags: TagsOf[Tags] = (a: Tags) => a

  implicit val tagsOfTuple: TagsOf[(String, String)] = { case (name, value) => Tags(Tag(name, value)) }

  implicit def tagsOfOption[A: TagsOf]: TagsOf[Option[A]] = (a: Option[A]) => a.fold(Tags.Empty)(_.tags)

  implicit def tagsOfEither[A: TagsOf, B: TagsOf]: TagsOf[Either[A, B]] = _.fold(_.tags, _.tags)
}
