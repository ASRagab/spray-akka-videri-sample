package org.business

import akka.actor.Actor
import spray.json.DefaultJsonProtocol

/**
  * Created by ASRagab on 3/29/16.
  */
case class PayloadData(id: Long, payload: String, timestamp: Long)
case class MetricsData(mostFrequent: String, leastFrequent: String, avgForMost: Float)
case object RequestCount

class MetricsService extends Actor {
  import MetricsService._

  override def receive = metrics(0)

  def metrics(count: Int): Receive = {
    case p: PayloadData =>
      context.become(metrics(count + 1))
      sender ! computeMetric(p)
    case RequestCount =>
      sender ! count.toString
  }
}

object MetricsService {
  def computeMetric(payloadData: PayloadData): Option[MetricsData] = {
    val charMap = payloadData.payload.replaceAll("\\s+", "").toLowerCase groupBy(c => c) mapValues(_.length)

    if(charMap == Map.empty[Char, Int])
      return None

    val (mostChar, mostCount) = charMap maxBy(_._2)
    val (leastChar, _) = charMap minBy(_._2)
    val numWords: Float = Math.max(payloadData.payload.split("\\w+").length.toFloat, 1.0F)
    val avgForMost = mostCount.toFloat / numWords

    Some(MetricsData(mostChar.toString, leastChar.toString, avgForMost))
  }
}

object JsonImplicits extends DefaultJsonProtocol {
  implicit val payloadFormat = jsonFormat3(PayloadData.apply)
  implicit val metricsFormat = jsonFormat3(MetricsData.apply)
}