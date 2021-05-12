
scalaVersion := "2.13.4"

name := "lizard-spok-bot"
organization := "ru.dm4x"
version := "1.0"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Ymacro-annotations",
)

val http4sVersion = "0.21.22"
val circeVersion = "0.13.0"
val doobieVersion = "0.9.0"
val catsVersion = "2.2.0"
val catsEffectVersion = "2.2.0"
val log4CatsVersion = "1.1.1"

libraryDependencies ++= Seq(
//  cats
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
//  http4s
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-jdk-http-client" % "0.3.6",
//  circe
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "io.circe" %% "circe-optics" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
//  doobie
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-h2" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
  "org.tpolecat" %% "doobie-scalatest" % doobieVersion % Test,
//  logs
  "io.chrisdavenport" %% "log4cats-slf4j" % log4CatsVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.1" cross CrossVersion.full)

run / fork := true
run / connectInput := true
