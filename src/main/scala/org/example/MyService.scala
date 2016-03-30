package org.example

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import spray.routing._
import spray.http._
import MediaTypes._

import scala.util.{Failure, Success}
import akka.util.Timeout
import org.business.{MetricsData, MetricsService, PayloadData, RequestCount}

import scala.concurrent.duration._
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling._
import org.business.JsonImplicits._

import scala.concurrent.Future


class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context

  def receive = runRoute(metricsRoutes)
  override val metricsService = context.actorOf(Props[MetricsService])
}

trait MyService extends HttpService {

  implicit def ec = actorRefFactory.dispatcher
  implicit val timeout: Timeout = 4 seconds
  val metricsService: ActorRef
  def handleRequestCount(metricsService: ActorRef): Future[String] = (metricsService ? RequestCount).mapTo[String]
  def handlePayloadData(metricsService: ActorRef, payloadData: PayloadData) = (metricsService ? payloadData).mapTo[Option[MetricsData]]


  val metricsRoutes = {

    path("metrics") {
      get {
        respondWithMediaType(`application/json`) {
          onComplete(handleRequestCount(metricsService)) {
            case Success(v) => complete(v)
            case Failure(e) => failWith(e)
          }
        }
      } ~
          post {
            entity(as[PayloadData]) { payload =>
              onComplete(handlePayloadData(metricsService, payload)) {
                case Success(value) => complete(value)
                case Failure(e) => failWith(e)
              }
            }
          }

    }
  }
}

