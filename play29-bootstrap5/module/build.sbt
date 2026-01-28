import scalariform.formatter.preferences._

name := """play-bootstrap"""

version := "1.6.1-P29-B5"

scalaVersion := "2.13.12"

crossScalaVersions := Seq("2.13.12", "3.3.1")

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies := libraryDependencies.value.filterNot(m => m.name == "twirl-api" || m.name == "play-server") ++ Seq(
  playCore % "provided",
  filters % "provided",
  "com.adrianhurt" %% "play-bootstrap-core" % "1.6.1-P29",
  specs2 % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala).disablePlugins(PlayFilters, PlayLogback, PlayAkkaHttpServer)

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)

PlayKeys.playOmnidoc := false

//*******************************
// Maven settings
//*******************************

sonatypeProfileName := "com.adrianhurt"

publishMavenStyle := true

organization := "com.adrianhurt"

description := "This is a collection of input helpers and field constructors for Play Framework to render Bootstrap 5 HTML code."

import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(GitHubHosting("playframework", "play-bootstrap", "contact@playframework.com"))

homepage := Some(url("https://playframework.github.io/play-bootstrap"))

licenses := Seq("Apache License" -> url("https://github.com/playframework/play-bootstrap/blob/master/LICENSE"))

startYear := Some(2014)

publishTo := sonatypePublishToBundle.value

Test / publishArtifact := false

pomIncludeRepository := { _ => false }

credentials += Credentials(Path.userHome / ".sbt" / "sonatype.credentials")
