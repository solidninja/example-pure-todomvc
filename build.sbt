import Dependencies._
import build._

Global / onChangedBuildSource := ReloadOnSourceChanges

// Remove when 2.13 unreachable code warnings with http4s is not longer present
def noFatalWarnings(options: Seq[String]): Seq[String] =
  options.filterNot(Set(
    "-Xfatal-warnings",
  ))

lazy val protocol = Project(
  id = "todomvc-protocol",
  base = file("protocol")
).settings(
  commonSettings,
  Seq(
    libraryDependencies ++= circe ++ scalatest,
    scalafmtOnCompile := true
  )
)



lazy val server = Project(
  id = "todomvc-server",
  base = file("server")
).settings(
  commonSettings,
  Seq(
    libraryDependencies ++= `cats-effect` ++ circe ++ doobie ++ http4sJvm ++ scalatest ++ testBlazeHttp ++ runtimeLogging,
    scalafmtOnCompile := true,
    Compile / compile / scalacOptions ~= noFatalWarnings,
  )
).dependsOn(protocol)
 .aggregate(protocol)
