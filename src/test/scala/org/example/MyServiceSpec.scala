package org.example

import akka.actor.Props
import org.business.{MetricsData, MetricsService, PayloadData}
import spray.testkit.ScalatestRouteTest
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling._
import org.business.JsonImplicits._
import org.scalatest.{FunSpecLike, MustMatchers}

class MyServiceSpec extends FunSpecLike with ScalatestRouteTest with MyService with MustMatchers {
  override def actorRefFactory = system
  override implicit def ec = executor
  override val metricsService = system.actorOf(Props[MetricsService])

  describe("My Service") {
    it("should return a count for GET requests to the /metrics path") {
      Get("/metrics") ~> sealRoute(metricsRoutes) ~> check {
        responseAs[String] must equal("0")
      }
    }

    it("should return metrics data for POST request to the /metrics path ") {
      Post("/metrics", PayloadData(1L, "Hello", 922L)) ~> sealRoute(metricsRoutes) ~> check {
        responseAs[Option[MetricsData]] must equal(Some(MetricsData("l","e",2.0F)))
      }
    }

    it("should leave GET requests to other paths unhandled") {
      Get("/kermit") ~> metricsRoutes ~> check {
        handled must be(false)
      }
    }
  }
}

