package scalaActors

import scala.actors.Actor
import scala.actors.Actor._

class HelloActor(name: String) extends Actor {
  def act() = {
    while (true) {
      receive {
        case actor: HelloActor =>
          actor.sayHello(name)
        case message: String if message.toLowerCase().startsWith("hello") =>
          println(f"Hello, I'm ${name}")
        case _ =>
          println("I don't understand")
      }
    }
  }
  private def sayHello(actorName: String) = {
    println(f"Hello, ${actorName}!")
  }
}

object Main extends App {
  val actor1 = new HelloActor("John")
  val actor2 = new HelloActor("Alex")
  actor1.start
  actor2.start

  actor1 ! "Hello"
  actor1 ! actor2

  actor2 ! "Hello"
  actor2 ! actor1

  actor1 ! "buenos dias"
}