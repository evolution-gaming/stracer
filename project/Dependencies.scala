import sbt._

object Dependencies {

  val scalatest     = "org.scalatest"       %% "scalatest"    % "3.0.8"
  val zipkin        = "io.zipkin.zipkin2"   % "zipkin"        % "2.18.0"
  val `cats-helper` = "com.evolutiongaming" %% "cats-helper"  % "1.0.4"
  val random        = "com.evolutiongaming" %% "random"       % "0.0.5"
  val configTools   = "com.evolutiongaming" %% "config-tools" % "1.0.4"
  val skafka        = "com.evolutiongaming" %% "skafka"       % "7.1.0"

  object Cats {
    private val version = "2.0.0"

    val core   = "org.typelevel" %% "cats-core"   % version
    val kernel = "org.typelevel" %% "cats-kernel" % version
    val macros = "org.typelevel" %% "cats-macros" % version
    val effect = "org.typelevel" %% "cats-effect" % "2.0.0"
  }

  object Scodec {
    val core = "org.scodec" %% "scodec-core" % "1.11.4"
    val bits = "org.scodec" %% "scodec-bits" % "1.1.12"
  }

  object PlayJsonTools {
    private val version = "0.3.13"

    val tools   = "com.evolutiongaming" %% "play-json-tools"   % version
    val generic = "com.evolutiongaming" %% "play-json-generic" % version
  }

  object Circe {
    private val version       = "0.12.2"
    private val versionExtras = "0.12.2"

    val core             = "io.circe" %% "circe-core"           % version
    val generic          = "io.circe" %% "circe-generic"        % version
    val parser           = "io.circe" %% "circe-parser"         % version
    val `generic-extras` = "io.circe" %% "circe-generic-extras" % versionExtras

    val all: Seq[ModuleID] = Seq(core, generic, parser, `generic-extras`)
  }
}
