import sbt._

object Dependencies {
  
  object Versions {
    val `cats-effect` = "2.0.0-M5"
    val circe = "0.12.0-M4"
    val doobie = "0.8.0-M3"
    val http4s = "0.21.0-M3"
    val scalatest = "3.0.8"
  }
  
  val http4sJvm = Seq(
    "org.http4s" %% "http4s-dsl" % Versions.http4s,
    "org.http4s" %% "http4s-circe" % Versions.http4s,
    "org.http4s" %% "http4s-blaze-client" % Versions.http4s,
    "org.http4s" %% "http4s-blaze-server" % Versions.http4s,
  )

  val `cats-effect` = Seq(
    "org.typelevel" %% "cats-effect" % Versions.`cats-effect`
  )
  
  val circe = Seq(
    "io.circe" %% "circe-literal" % Versions.circe,
    "io.circe" %% "circe-generic" % Versions.circe,
  )

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core" % Versions.doobie,
    "org.tpolecat" %% "doobie-h2" % Versions.doobie,
    "org.tpolecat" %% "doobie-hikari" % Versions.doobie,
  )

  val scalatest = Seq(
    "org.scalatest" %% "scalatest" % Versions.scalatest % Test,
    "org.tpolecat" %% "doobie-scalatest" % Versions.doobie % Test,
  )

  val testBlazeHttp = Seq(
    "org.http4s" %% "http4s-blaze-client" % Versions.http4s % Test,
  )

  val runtimeLogging = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  )
}
