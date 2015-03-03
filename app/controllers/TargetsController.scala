package controllers

import models.{Measurement, TargetRepository}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TargetsController extends Controller {
  def getAll(measurement_id: String) = Action.async {
    implicit request =>
      if (measurement_id.isEmpty) {
        Future(BadRequest(JsonErrorMessage("measurement_id must be a non-empty string")))
      } else {
        Measurement.describe(measurement_id) map {
          case Some(dataFieldsAndTypes) => Ok(Json.toJson(TargetRepository.getByTargetFields(dataFieldsAndTypes.keys.toSeq)))
          case None                     => NotFound(JsonErrorMessage(s"Unknown measurement_id $measurement_id"))
        } recover {
          case ex: Throwable =>
            Logger.error(s"TargetsController: failed to describe measurement $measurement_id: ", ex)
            InternalServerError(JsonErrorMessage(ex))
        }
      }
  }
}
