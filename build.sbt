val http4sVersion = "0.21.22"
val circeVersion = "0.13.0"
val doobieVersion = "0.9.0"
val catsVersion = "2.2.0"
val catsEffectVersion = "2.2.0"
val log4CatsVersion = "1.1.1"

lazy val root = (project in file("."))
  .settings(
    name := "lizard-spock-bot",
    organization := "ru.dm4x",
    version := "1.0",
    scalaVersion := "2.13.4",

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-Ymacro-annotations",
    ),

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
      //  tapir
      "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.18.1",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.18.0-M15",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.18.0-M15",

      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
    ),

    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.1" cross CrossVersion.full),

    docker / dockerfile := {
      val artifact: File = stage.value
      val artifactTargetPath = s"/app/${artifact.name}"

      new Dockerfile {
        from("adoptopenjdk/openjdk15")
        add(artifact, artifactTargetPath)
        entryPoint("java", "-jar", artifactTargetPath)
        expose(8090)
      }
    },
  ).enablePlugins(JavaAppPackaging)

run / fork := true
run / connectInput := true
