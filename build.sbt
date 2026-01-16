import Dependencies._

ThisBuild / scalaVersion     := "2.12.21"
ThisBuild / organization     := "com.cloud-apim"
ThisBuild / organizationName := "Cloud-APIM"
ThisBuild / description := "SecLang Engine WAF is a ModSecurity-compatible Web Application Firewall (WAF) library for the JVM, written in Scala."
ThisBuild / homepage := Some(url("https://github.com/cloud-apim/seclang-engine"))
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / developers := List(
  Developer(
    "mathieuancelin",
    "Mathieu ANCELIN",
    "mathieu@cloud-apim.com",
    url("https://github.com/mathieuancelin")
  ),
  Developer(
    "cloud-apim",
    "Cloud-APIM Team",
    "contact@cloud-apim.com",
    url("https://github.com/cloud-apim")
  )
)
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/cloud-apim/seclang-engine"),
    "scm:git@github.com:cloud-apim/seclang-engine.git"
  )
)
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}

usePgpKeyHex("235E536BA3E43419FD649B903C82DD5C11569EF6")

lazy val root = (project in file("."))
  .settings(
    name := "seclang-engine",
    libraryDependencies ++= Seq(
      "com.comcast" %% "ip4s-core" % "3.2.0",
      "org.antlr" % "antlr4" % "4.13.2",
      "com.typesafe.play" %% "play-json" % "2.9.3",
      "com.cloud-apim" % "libinjection-jvm" % "1.2.0",
      "com.github.blemale" %% "scaffeine" % "4.0.2",
      "org.apache.commons" % "commons-text" % "1.15.0",
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.13.4" % Test,
      munit % Test
    ),
    Compile / doc / scalacOptions ++= Seq(
      "-doc-title", "SecLang Engine",
      "-doc-version", version.value
    )
  )
