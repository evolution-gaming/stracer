import sbt.*

object Dependencies {

  val scalatest = "org.scalatest" %% "scalatest" % "3.2.19"
  val zipkin = "io.zipkin.zipkin2" % "zipkin" % "2.21.6"
  val `cats-helper` = "com.evolutiongaming" %% "cats-helper" % "3.12.2"
  val random = "com.evolution" %% "random" % "1.0.5"
  val configTools = "com.evolutiongaming" %% "config-tools" % "1.0.5"
  val skafka = "com.evolutiongaming" %% "skafka" % "17.2.0"

  object Cats {
    private val version = "2.13.0"
    val core = "org.typelevel" %% "cats-core" % version
  }

  object CatsEffect {
    private val version = "3.5.7"
    val effect = "org.typelevel" %% "cats-effect" % version
  }

  object Scodec {
    val bits = "org.scodec" %% "scodec-bits" % "1.2.4"

    // see https://github.com/scodec/scodec/issues/365
    object Scala2 {
      val core = "org.scodec" %% "scodec-core" % "1.11.11" // the last scodec-core version built for 2.13
    }

    object Scala3 {
      val core = "org.scodec" %% "scodec-core" % "2.3.3"
    }
  }

  object PlayJsonTools {
    private val version = "1.2.3"

    val tools = "com.evolution" %% "play-json-tools" % version
  }

  object Circe {
    private val version = "0.14.14"

    val core = "io.circe" %% "circe-core" % version
    val generic = "io.circe" %% "circe-generic" % version
    val parser = "io.circe" %% "circe-parser" % version

    val all: Seq[ModuleID] = Seq(core, generic, parser)
  }

  object Pureconfig {
    private val version = "0.17.8"

    object Scala2 {
      val pureconfig = "com.github.pureconfig" %% "pureconfig" % version
    }

    object Scala3 {
      val core = "com.github.pureconfig" %% "pureconfig-core" % version
      val generic = "com.github.pureconfig" %% "pureconfig-generic-scala3" % version
    }
  }
}
