package controllers

import play.api.libs.json.{Json, JsObject}

object JsonErrorMessage {
  /**
   * Create a JsObject to represent an error in the following format :
   * {{{
   *   {
   *       "message": ...,
   *       "stack": [...]
   *   }
   * }}}
   * @param message Error message
   * @param stackTrace Error stack trace
   * @return
   */
  def apply(message: String, stackTrace: Option[Array[StackTraceElement]] = None): JsObject = {
    Json.obj("message" -> message) ++ (stackTrace match {
      case None => Json.obj()
      case Some(s) => Json.obj("stack" -> s.map(_.toString))
    })
  }

  /**
   * Shortcut for {{{apply(throwable.getMessage, Some(throwable.getStackTrace))}}}
   * @param throwable Throwable
   */
  def apply(throwable: Throwable): JsObject = {
    apply(throwable.getMessage, Some(throwable.getStackTrace))
  }
}
