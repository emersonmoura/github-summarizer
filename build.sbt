import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.scalac"
ThisBuild / organizationName := "summarizer"

lazy val root = (project in file("."))
  .settings(
    name := "github-summarizer",
    libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.11",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.26",
    libraryDependencies += scalaTest % Test,
  )

