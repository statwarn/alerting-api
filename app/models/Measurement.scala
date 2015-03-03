package models

import play.api.Play.{configuration, current}
import play.api.libs.ws.WS

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Measurement {
  lazy val monitoringAPIBaseURL = configuration.getString("monitoring_api.endpoint").get + "/api/v1"

  /**
   * Use the monitoring API to describe the measurement with the given id,
   * and retrieve the "data" and "metadata" fields
   * @param measurement_id Measurement id
   * @return
   */
  def describe(measurement_id: String): Future[Option[Map[String, String]]] = {
    val describeRouteURL = s"$monitoringAPIBaseURL/measurements/$measurement_id/describe"

    WS.url(describeRouteURL).get() map {
      response => response.status match {
        case 200 => Some(response.json.as[Map[String, String]])
        case 404 => None
      }
    } recoverWith {
      case matchError: MatchError => Future.failed(new Exception("Invalid status code when contacting monitoring-api"))
    }
  }
}
