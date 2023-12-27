ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.0"

lazy val root = (project in file("."))
  .settings(
    name := "akkaActors"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  "ch.qos.logback" % "logback-classic" % "1.2.11"
)
