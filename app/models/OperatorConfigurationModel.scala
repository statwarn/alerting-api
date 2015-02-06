package models

import play.api.libs.json.{Json, Format, JsObject}

case class OperatorConfigurationModel(configuration: JsObject) {
}

object OperatorConfigurationModel {
  implicit val jsonFormat: Format[OperatorConfigurationModel] = Json.format[OperatorConfigurationModel]
}
