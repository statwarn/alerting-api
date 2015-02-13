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

// Akka-RabbitMQ - https://github.com/thenewmotion/akka-rabbitmq
resolvers += "The New Motion Public Repo" at "http://nexus.thenewmotion.com/content/groups/public/"

libraryDependencies += "com.thenewmotion.akka" %% "akka-rabbitmq" % "1.2.4"
