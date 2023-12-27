package akkaActors

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props

class HelloActor extends Actor {
  def receive: Receive = {
    case message: String if message.toLowerCase().startsWith("hello") =>
      println(f"Hello, I'm ${self.path.name}")
    case actor: ActorRef =>
      sayHello(actor)
    case _ =>
      println("I don't understand")
  }
  def sayHello(actor: ActorRef): Unit = {
    println(f"Hello, ${actor.path.name}!")
  }
}

object Main extends App {
  val system = ActorSystem("System")
  val actor1 = system.actorOf(Props[HelloActor], name = "John")
  val actor2 = system.actorOf(Props[HelloActor], name = "Alex")

  actor1 ! "Hello"
  actor1 ! actor2
  actor2 ! "Hello"
  actor2 ! actor1

  actor1 ! "buenos dias"
}