package com.evolutiongaming.stracer

object Tags {

  val Empty: Tags = List.empty

  def apply(tag: Tag): Tags = List(tag)

  def apply(tags: (String, String)*): Tags = {
    tags.map { case (value, name) => Tag(value = value, name = name) }.toList
  }
}