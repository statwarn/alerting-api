package controllers

import java.util.UUID

import models._
import play.api.libs.json._
import play.api.mvc._

object AlertsController extends Controller {
  /**
   * Get all alerts
   * @return
   */
  def getAll(measurement_id: Seq[UUID]) = Action {
    implicit request =>
      Ok(Json.toJson(AlertRepository.getAll(measurement_ids = measurement_id)))
  }

  /**
   * Get the alert with the given id
   * @param alertId Id of the alert to retrieve
   * @return
   */
  def getById(alertId: UUID) = Action {
    implicit request =>
      AlertRepository.getById(alertId).headOption.fold({
        NotFound: Result
      })({
        alert =>
          alert.alertModel.deletedAt.fold(Ok(Json.toJson(alert)))(_ => Gone)
      })
  }

  /**
   * Create or update an alert
   * @param alertId Id of the alert to create/update
   * @return
   */
  def createOrUpdate(alertId: UUID) = Action(BodyParsers.parse.json) {
    implicit request: Request[JsValue] =>
      // Copy the alertId from the route parameter into the JSON
      request.body.asOpt[JsObject].map({
        bodyAsObject => (bodyAsObject + ("alert_id" -> JsString(alertId.toString))).validate[AlertCreate].fold({
          errors => BadRequest(JsError.toFlatJson(errors))
        }, {
          alertCreate => Ok(Json.toJson(AlertRepository.createOrUpdate(alertCreate)))
        })
      }).getOrElse(BadRequest(Json.obj("message" -> "Invalid JSON format, expecting an object")))
  }

  /**
   *
   * @param alertId Id of the alert to delete
   * @return
   */
  def delete(alertId: UUID) = Action {
    implicit request =>
      AlertRepository.delete(alertId) match {
        case AlertRepository.DeleteResultStatus.NotFound => NotFound
        case AlertRepository.DeleteResultStatus.Untouched => Gone
        case AlertRepository.DeleteResultStatus.Updated => Ok
      }
  }
}
