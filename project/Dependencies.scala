import sbt._

object Dependencies {
  val http4sJvm = Seq(
    "org.http4s" %% "http4s-dsl" % "0.18.0-M1",
    "org.http4s" %% "http4s-circe" % "0.18.0-M1",
    "org.http4s" %% "http4s-blaze-client" % "0.18.0-M1",
    "org.http4s" %% "http4s-blaze-server" % "0.18.0-M1",
  )

  val circe = Seq(
    "io.circe" %% "circe-literal" % "0.9.0-M1",
    "io.circe" %% "circe-generic" % "0.9.0-M1",
  )

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core" % "0.5.0-M6",
    "org.tpolecat" %% "doobie-h2" % "0.5.0-M6",
    "org.tpolecat" %% "doobie-hikari" % "0.5.0-M6",
  )

  val fs2 = Seq(
    "co.fs2" %% "fs2-core" % "0.10.0-M6",
  )

  val scalatest = Seq(
    "org.scalatest" %% "scalatest" % "3.0.3" % "test",
    "org.tpolecat" %% "doobie-scalatest" % "0.5.0-M6" % "test",
  )

  val testBlazeHttp = Seq(
    "org.http4s" %% "http4s-blaze-client" % "0.18.0-M1" % "test",
  )

  val runtimeLogging = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  )
}
