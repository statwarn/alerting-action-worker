name := """alerting-action-worker"""

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.9" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.play" %% "play-json" % "2.4.0-M2",
  "com.typesafe.play" %% "play-ws" % "2.4.0-M2"
    excludeAll(
      ExclusionRule(organization = "org.specs2"),
      ExclusionRule(organization = "org.mockito")
    )
  )

// Akka-RabbitMQ - https://github.com/thenewmotion/akka-rabbitmq
resolvers += "The New Motion Public Repo" at "http://nexus.thenewmotion.com/content/groups/public/"

libraryDependencies += "com.thenewmotion.akka" %% "akka-rabbitmq" % "1.2.4"
