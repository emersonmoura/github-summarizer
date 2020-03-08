import Dependencies._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.scalac"
ThisBuild / organizationName := "summarizer"

lazy val root = (project in file("."))
  .settings(
      name := "github-summarizer",
      libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.11",
      libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.26",
      libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",
      libraryDependencies += "io.spray" %% "spray-json" % "1.3.5",
      libraryDependencies += "com.github.blemale" %% "scaffeine" % "3.1.0" % "compile",
      libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.26" % Test,
      libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "10.1.11" % Test,
      libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test,
      libraryDependencies += scalaTest % Test
  )

