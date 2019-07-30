import sbt._

object Dependencies {

  val scalatest     = "org.scalatest"       %% "scalatest"    % "3.0.8"
  val zipkin        = "io.zipkin.zipkin2"    % "zipkin"       % "2.15.0"
  val `cats-helper` = "com.evolutiongaming" %% "cats-helper"  % "0.0.23"
  val random        = "com.evolutiongaming" %% "random"       % "0.0.3"
  val configTools   = "com.evolutiongaming" %% "config-tools" % "1.0.3"
  val skafka        = "com.evolutiongaming" %% "skafka"       % "6.0.2"

  object Cats {
    private val version = "1.6.1"
    val core   = "org.typelevel" %% "cats-core"   % version
    val kernel = "org.typelevel" %% "cats-kernel" % version
    val macros = "org.typelevel" %% "cats-macros" % version
    val effect = "org.typelevel" %% "cats-effect" % "1.3.1"
  }
  
  object Scodec {
    val core = "org.scodec" %% "scodec-core" % "1.11.4"
    val bits = "org.scodec" %% "scodec-bits" % "1.1.12"
  }

  object PlayJsonTools {
    private val version = "0.3.11"
    val tools   = "com.evolutiongaming" %% "play-json-tools"   % version
    val generic = "com.evolutiongaming" %% "play-json-generic" % version
  }
}