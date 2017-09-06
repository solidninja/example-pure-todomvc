import Dependencies._
import build._

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
    libraryDependencies ++= circe ++ doobie ++ http4sJvm ++ fs2 ++ scalatest ++ testBlazeHttp ++ runtimeLogging,
    scalafmtOnCompile := true
  )
).dependsOn(protocol)
 .aggregate(protocol)
