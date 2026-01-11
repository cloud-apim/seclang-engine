import Dependencies._

ThisBuild / scalaVersion     := "2.12.21"
ThisBuild / version          := "1.0.0-dev"
ThisBuild / organization     := "com.cloud-apim"
ThisBuild / organizationName := "Cloud-APIM"

ThisBuild / versionScheme := Some("early-semver")
ThisBuild / homepage := Some(url("https://github.com/cloud-apim/seclang-engine"))
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / developers := List(
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
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / publishMavenStyle := true
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / sonatypeProfileName := "com.cloud-apim"
//ThisBuild / pgpPassphrase := Some(sys.env("CLOUDAPIM_GPG_KEY_PASSPHRASE").toCharArray)
ThisBuild / useGpgAgent := true

lazy val root = (project in file("."))
  .settings(
    name := "seclang-engine",
    libraryDependencies ++= Seq(
      "com.comcast" %% "ip4s-core" % "3.2.0",
      "org.antlr" % "antlr4" % "4.13.2",
      "com.typesafe.play" %% "play-json" % "2.9.3",
      "com.cloud-apim" % "libinjection-jvm" % "1.1.0",
      "com.github.blemale" %% "scaffeine" % "4.0.2",
      "org.apache.commons" % "commons-text" % "1.15.0",
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.13.4" % Test,
      munit % Test
    ),
    crossScalaVersions := Seq("2.12.21", "2.13.15"),
    Compile / doc / scalacOptions ++= Seq(
      "-doc-title", "SecLang Engine",
      "-doc-version", version.value
    )
  )
