import Dependencies._

lazy val modules: List[ProjectReference] = List(
  `stracer`,
  `stracer-play-json`,
  `stracer-circe`
)

lazy val root = project
  .in(file("."))
  .settings(skip in publish := true)
  .settings(thisBuildSettings)
  .aggregate(modules: _*)

lazy val `stracer` = project
  .settings(
    libraryDependencies ++= Seq(
      skafka,
      zipkin,
      random,
      Scodec.core,
      Scodec.bits,
      `cats-helper`,
      Cats.core,
      Cats.kernel,
      Cats.macros,
      Cats.effect,
      configTools,
      Pureconfig.pureconfig,
      scalatest % Test))

lazy val `stracer-play-json` = project
  .dependsOn(`stracer`)
  .settings(
    libraryDependencies ++= Seq(
      PlayJsonTools.tools,
      scalatest % Test))

lazy val `stracer-circe` = project
  .dependsOn(`stracer`)
  .settings(
    libraryDependencies ++= Circe.all ++ Seq(scalatest % Test))

val thisBuildSettings = inThisBuild(
  Seq(
    name := "stracer",
    description := "Library for distributed tracing in Scala",
    organization := "com.evolutiongaming",
    homepage := Some(new URL("http://github.com/evolution-gaming/stracer")),
    startYear := Some(2019),
    organizationName := "Evolution Gaming",
    organizationHomepage := Some(url("http://evolutiongaming.com")),
    bintrayOrganization := Some("evolutiongaming"),
    scalaVersion := crossScalaVersions.value.head,
    crossScalaVersions := Seq("2.13.1", "2.12.10"),
    releaseCrossBuild := true,
    licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT"))),
    resolvers += Resolver.bintrayRepo("evolutiongaming", "maven")
  )
)
