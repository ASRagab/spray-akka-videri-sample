package org.business

import org.scalatest.{FunSpecLike, ShouldMatchers}

/**
  * Created by ASRagab on 3/30/16.
  */
class MetricsServiceTest extends FunSpecLike with ShouldMatchers {
  import MetricsService._

  describe("Metrics Service Computation") {
    it("should create MetricsData if valid PayloadData is parameter") {
      val payloadData = PayloadData(1L, "test", 0L)

      val result = computeMetric(payloadData)
      result match {
        case r @ Some(MetricsData(_, _, _)) =>
          r.get.mostFrequent should equal("t")
          r.get.leastFrequent should equal("e")
          r.get.avgForMost should equal(2.0F)
        case None => fail("Test failed")
      }
    }

    it("should return None if payload is empty string") {
      val payloadData = PayloadData(1L, "", 0L)

      val result = computeMetric(payloadData)
      result should be(None)
    }
  }
}
