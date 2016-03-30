package org.example

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import language.postfixOps

object Boot extends App {

  implicit val system = ActorSystem("actor-system")
  val service = system.actorOf(Props[MyServiceActor], "metrics-service")

  implicit val timeout = Timeout(5 seconds)
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
