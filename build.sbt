name := """alerting-api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm
)

// We need a specific resolver for Scalaz when using Play! 2.4.0-M2
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
