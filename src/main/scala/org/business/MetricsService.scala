package org.business

import akka.actor.Actor
import spray.json.DefaultJsonProtocol

/**
  * Created by ASRagab on 3/29/16.
  */
case class PayloadData(id: Long, payload: String, timestamp: Long)
case class MetricsData(mostFrequent: String, leastFrequent: String, AvgForMost: Float)
case object RequestCount


class MetricsService extends Actor {
  import MetricsService._

  var count = 0  //This could be improved

  override def receive = {
    case p: PayloadData =>
      count += 1
      sender ! computeMetric(p)
    case RequestCount =>
      sender ! count.toString
  }
}


object MetricsService {
  def computeMetric(payloadData: PayloadData): Option[MetricsData] = {
    val charMap = payloadData.payload.replaceAll("\\s+", "") groupBy(c => c) mapValues(_.length)

    if(charMap == Map.empty[Char, Int])
      return None

    val (mostChar, mostCount) = charMap maxBy(_._2)
    val (leastChar, leastCount) = charMap minBy(_._2)
    val numWords: Float = Math.max(payloadData.payload.split("\\w+").length.toFloat, 1.0F)
    val avgForMost = mostCount.toFloat / numWords

    Some(MetricsData(mostChar.toString, leastChar.toString, avgForMost))
  }
}

object JsonImplicits extends DefaultJsonProtocol {
  implicit val payloadFormat = jsonFormat3(PayloadData.apply)
  implicit val metricsFormat = jsonFormat3(MetricsData.apply)
}