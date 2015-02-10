package models

import play.api.libs.json._

case class Alert(alertModel: AlertModel, triggers: Seq[TriggerModel], actions: Seq[AlertActionModel]) {
}

object Alert {
  val jsonReads: Reads[Alert] = Reads({
    jsValue =>
      JsSuccess(Alert(jsValue.as[AlertModel], (jsValue \ "triggers").as[Seq[TriggerModel]], (jsValue \ "actions").as[Seq[AlertActionModel]]))
  })

  val jsonWrites: Writes[Alert] = Writes({
    alert =>
      Json.toJson(alert.alertModel).as[JsObject] ++
        Json.obj("triggers" -> Json.toJson(alert.triggers)) ++
        Json.obj("actions" -> Json.toJson(alert.actions))
  })

  implicit val jsonFormat: Format[Alert] = Format(jsonReads, jsonWrites)
}
