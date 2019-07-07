import sbt._

object Dependencies {

  val scalatest     = "org.scalatest"       %% "scalatest"    % "3.0.8"
  val machinist     = "org.typelevel"       %% "machinist"    % "0.6.8"
  val `cats-par`    = "io.chrisdavenport"   %% "cats-par"     % "0.2.1"
  val zipkin        = "io.zipkin.zipkin2"    % "zipkin"       % "2.15.0"
  val `cats-helper` = "com.evolutiongaming" %% "cats-helper"  % "0.0.14"
  val random        = "com.evolutiongaming" %% "random"       % "0.0.2"
  val configTools   = "com.evolutiongaming" %% "config-tools" % "1.0.3"

  object Skafka {
    private val version = "5.0.2"
    val skafka      = "com.evolutiongaming" %% "skafka"            % version
    val logging     = "com.evolutiongaming" %% "skafka-logging"    % version
    val `play-json` = "com.evolutiongaming" %% "skafka-play-json"  % version
    val prometheus  = "com.evolutiongaming" %% "skafka-prometheus" % version
  }

  object Cats {
    private val version = "1.6.1"
    val core   = "org.typelevel" %% "cats-core"   % version
    val kernel = "org.typelevel" %% "cats-kernel" % version
    val macros = "org.typelevel" %% "cats-macros" % version
    val effect = "org.typelevel" %% "cats-effect" % "1.3.1"
  }

  object Jaeger {
    private val version = "0.34.0"
    val core           = "io.jaegertracing" % "jaeger-core"           % version
    val thrift         = "io.jaegertracing" % "jaeger-thrift"         % version
    val client         = "io.jaegertracing" % "jaeger-client"         % version
    val tracerresolver = "io.jaegertracing" % "jaeger-tracerresolver" % version
    val zipkin         = "io.jaegertracing" % "jaeger-zipkin"         % version
  }

  object Opentracing {
    private val version = "0.31.0"
    val api            = "io.opentracing"         % "opentracing-api"            % version
    val noop           = "io.opentracing"         % "opentracing-noop"           % version
    val util           = "io.opentracing"         % "opentracing-util"           % version
    val tracerresolver = "io.opentracing.contrib" % "opentracing-tracerresolver" % "0.1.5"
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


  object KafkaJournal {
    private val version = "0.0.68"
    val journal = "com.evolutiongaming" %% "kafka-journal" % version
  }
}