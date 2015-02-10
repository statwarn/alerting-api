package models

import java.util.UUID

import play.api.libs.json.{Json, Format, JsObject}

case class AlertCreate(
                        alert_id: UUID,
                        name: String,
                        measurement_id: UUID,
                        activated: Boolean,
                        actions: Seq[ActionCreate],
                        triggers: Seq[TriggerCreate]
                        ) {
}

object AlertCreate {
  implicit val jsonFormat: Format[AlertCreate] = Json.format[AlertCreate]
}
