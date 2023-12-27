name := "scalaActors"
version := "1.0"
resolvers += "Akka library repository".at("https://repo.akka.io/maven")
fork := true

scalaVersion := s"2.10.7"
libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-actors" % "2.10.7"
)
