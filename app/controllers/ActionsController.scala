package controllers

import models.ActionRepository
import play.api.libs.json.Json
import play.api.mvc._

object ActionsController extends Controller {
  def getAll() = Action {
    implicit request =>
      Ok(Json.toJson(ActionRepository.getAll()))
  }
}
