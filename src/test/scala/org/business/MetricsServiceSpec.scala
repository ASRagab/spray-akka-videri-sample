package org.business

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, MustMatchers}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
import language.postfixOps

/**
  * Created by ASRagab on 3/30/16.
  */
class MetricsServiceSpec extends TestKit(ActorSystem("test")) with ImplicitSender
    with FunSpecLike
    with BeforeAndAfterAll
    with MustMatchers {

  implicit val timeout: Timeout = 2 seconds

  override protected def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }

  describe("MetricsService Actor") {
    it("should increment counter when Payload request received") {
      val metricsService = system.actorOf(Props[MetricsService])

      metricsService ! PayloadData(1L, "test", 10L)
      metricsService ! PayloadData(1L, "test", 10L)
      metricsService ! PayloadData(1L, "test", 10L)

      val result = Await.result(metricsService ? RequestCount, 4 seconds)
      result.toString must equal("3")
    }
  }

  it("should return Metrics Data when payloadData request received") {
    val metricsService = system.actorOf(Props[MetricsService])
    val result = Await.result(metricsService ? PayloadData(1L, "test trial", 10L), 4 seconds)

    val r = result.asInstanceOf[Option[MetricsData]]
    r.get.avgForMost must equal(1.5F)
  }

  it("should NOT increment counter if RequestCount sent") {
    val metricsService = system.actorOf(Props[MetricsService])

    metricsService ! RequestCount
    metricsService ! RequestCount
    metricsService ! RequestCount

    val result: Any = Await.result(metricsService ? RequestCount, 1 seconds)
    result.toString must equal("0")
  }
}
