import Dependencies._

lazy val modules: List[ProjectReference] = List(
  `stracer`,
  `stracer-play-json`,
  `stracer-circe`
)

lazy val root = project
  .in(file("."))
  .settings(name := "stracer")
  .settings(commonSettings)
  .settings(skip in publish := true)
  .aggregate(modules: _*)

lazy val `stracer` = project
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      skafka,
      zipkin,
      random,
      Scodec.core,
      Scodec.bits,
      `cats-helper`,
      Cats.core,
      Cats.effect,
      configTools,
      Pureconfig.pureconfig,
      scalatest % Test))

lazy val `stracer-play-json` = project
  .dependsOn(`stracer`)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      PlayJsonTools.tools,
      scalatest % Test))

lazy val `stracer-circe` = project
  .dependsOn(`stracer`)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Circe.all ++ Seq(scalatest % Test))

val commonSettings = Seq(
  description := "Library for distributed tracing in Scala",
  organization := "com.evolutiongaming",
  homepage := Some(new URL("http://github.com/evolution-gaming/stracer")),
  startYear := Some(2019),
  organizationName := "Evolution Gaming",
  organizationHomepage := Some(url("http://evolutiongaming.com")),
  bintrayOrganization := Some("evolutiongaming"),
  scalaVersion := crossScalaVersions.value.head,
  crossScalaVersions := Seq("2.13.5", "2.12.12"),
  releaseCrossBuild := true,
  licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT"))),
  resolvers += Resolver.bintrayRepo("evolutiongaming", "maven"))
