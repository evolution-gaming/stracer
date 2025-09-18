import Dependencies.*

ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / versionPolicyIntention := Compatibility.BinaryCompatible

lazy val modules: List[ProjectReference] = List(
  `stracer`,
  `stracer-play-json`,
  `stracer-circe`,
  `stracer-kafka`,
)

lazy val root = project
  .in(file("."))
  .settings(name := "stracer")
  .settings(commonSettings)
  .settings(alias)
  .settings(publish / skip := true)
  .aggregate(modules *)

lazy val `stracer` = project
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      zipkin,
      random,
      Scodec.bits,
      `cats-helper`,
      Cats.core,
      CatsEffect.effect,
      configTools,
      scalatest % Test,
    ),
    libraryDependencies ++= crossSettings(
      scalaVersion = scalaVersion.value,
      if2 = Seq(
        Pureconfig.Scala2.pureconfig,
        Scodec.Scala2.core,
      ),
      if3 = Seq(
        Pureconfig.Scala3.core,
        Pureconfig.Scala3.generic,
        Scodec.Scala3.core,
      ),
    ),
  )

lazy val `stracer-play-json` = project
  .dependsOn(`stracer`)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      PlayJsonTools.tools,
      scalatest % Test,
    ),
  )

lazy val `stracer-circe` = project
  .dependsOn(`stracer`)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Circe.all ++ Seq(scalatest % Test),
  )

lazy val `stracer-kafka` = project
  .dependsOn(`stracer`)
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(skafka, scalatest % Test))

val commonSettings = Seq(
  description := "Library for distributed tracing in Scala",
  organization := "com.evolutiongaming",
  homepage := Some(url("https://github.com/evolution-gaming/stracer")),
  startYear := Some(2019),
  organizationName := "Evolution",
  organizationHomepage := Some(url("https://evolution.com")),
  scalaVersion := crossScalaVersions.value.head,
  crossScalaVersions := Seq("2.13.16", "3.3.6"),
  scalacOptions ++= Seq(
    "-release:17",
    "-deprecation",
  ),
  scalacOptions ++= crossSettings(
    scalaVersion = scalaVersion.value,
    if2 = Seq(
      "-Xsource:3",
    ),
    // Good compiler options for Scala 2.13 are coming from com.evolution:sbt-scalac-opts-plugin:0.0.9,
    // but its support for Scala 3 is limited, especially what concerns linting options.
    //
    // If Scala 3 is made the primary target, good linting scalac options for it should be added first.
    if3 = Seq(
      "-Ykind-projector:underscores",

      // disable new brace-less syntax:
      // https://alexn.org/blog/2022/10/24/scala-3-optional-braces/
      "-no-indent",

      // improve error messages:
      "-explain",
      "-explain-types",
    ),
  ),
  licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT"))),
  publishTo := Some(Resolver.evolutionReleases),
  scalacOptsFailOnWarn := Some(false),
)

val alias: Seq[sbt.Def.Setting[?]] =
  addCommandAlias("check", "all versionPolicyCheck Compile/doc scalafmtCheckAll scalafmtSbtCheck") ++
    addCommandAlias("build", "+all compile test") ++
    addCommandAlias("fmt", "+all scalafmtAll scalafmtSbt")

def crossSettings[T](scalaVersion: String, if3: T, if2: T): T = {
  scalaVersion match {
    case version if version.startsWith("3") => if3
    case _ => if2
  }
}
