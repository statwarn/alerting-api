package controllers

import play.api.mvc.{Controller, Action}

object OptionsController extends Controller {
  def options(path: String) = Action {
    implicit request =>
      // Access-Control headers are handled by the CORSFilter, so we just reply OK
      Ok
  }
}
