import Dependencies._

ThisBuild / scalaVersion     := "2.12.21"
ThisBuild / version          := "1.0.0-dev"
ThisBuild / organization     := "com.cloud-apim"
ThisBuild / organizationName := "Cloud-APIM"

lazy val root = (project in file("."))
  .settings(
    name := "scala-seclang-engine",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "fastparse" % "3.1.1",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
      "org.antlr" % "antlr4" % "4.13.2",
      munit % Test
    )
  )
