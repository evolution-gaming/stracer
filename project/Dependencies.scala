import sbt._

object Dependencies {

  val scalatest     = "org.scalatest"       %% "scalatest"    % "3.2.15"
  val zipkin        = "io.zipkin.zipkin2"   % "zipkin"        % "2.21.6"
  val `cats-helper` = "com.evolutiongaming" %% "cats-helper"  % "3.5.0"
  val random        = "com.evolutiongaming" %% "random"       % "1.0.0"
  val configTools   = "com.evolutiongaming" %% "config-tools" % "1.0.4"
  val skafka        = "com.evolutiongaming" %% "skafka"       % "17.1.2"


  object Cats {
    private val version = "2.8.0"
    val core = "org.typelevel" %% "cats-core" % version
  }

  object CatsEffect {
    private val version = "3.5.3"
    val effect = "org.typelevel" %% "cats-effect" % version
  }

  object Scodec {
    val core = "org.scodec" %% "scodec-core" % "1.11.7"
    val bits = "org.scodec" %% "scodec-bits" % "1.1.18"
  }

  object PlayJsonTools {
    private val version = "1.1.1"

    val tools   = "com.evolution" %% "play-json-tools"   % version
  }

  object Circe {
    private val version       = "0.14.9"
    private val versionExtras = "0.14.1"

    val core             = "io.circe" %% "circe-core"           % version
    val generic          = "io.circe" %% "circe-generic"        % version
    val parser           = "io.circe" %% "circe-parser"         % version
    val `generic-extras` = "io.circe" %% "circe-generic-extras" % versionExtras

    val all: Seq[ModuleID] = Seq(core, generic, parser, `generic-extras`)
  }

  object Pureconfig {
    private val version = "0.17.3"
    val pureconfig = "com.github.pureconfig" %% "pureconfig"      % version
    val cats       = "com.github.pureconfig" %% "pureconfig-cats" % version
  }
}
