package models

import play.api.libs.json.{Json, Format, JsObject}

case class ActionConfigurationModel(configuration: JsObject) {
}

object ActionConfigurationModel {
  implicit val jsonFormat: Format[ActionConfigurationModel] = Json.format[ActionConfigurationModel]
}
