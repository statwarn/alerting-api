package controllers

import java.util.UUID

import models._
import play.api.data.{Mapping, Forms, Form}
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc._

object AlertsController extends Controller {
  /**
   * Get all alerts
   * @return
   */
  def getAll = Action {
    implicit request => ???
  }

  /**
   * Get the alert with the given id
   * @param alertId Id of the alert to retrieve
   * @return
   */
  def getById(alertId: UUID) = Action {
    implicit request => ???
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
          alertCreate => Ok(Json.toJson(AlertRepository.create(alertCreate)))
        })
      }).getOrElse(BadRequest(Json.obj("message" -> "Invalid JSON format, expecting an object")))
  }

  /**
   *
   * @param alertId Id of the alert to delete
   * @return
   */
  def delete(alertId: UUID) = Action {
    implicit request => ???
  }
}
