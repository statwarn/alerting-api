package models

import play.api.libs.json.{Json, Format, JsObject}

case class TriggerCreate(
                          operator: JsObject,
                          target: String
                          ) {
}

object TriggerCreate {
  implicit val jsonFormat: Format[TriggerCreate] = Json.format[TriggerCreate]
}
