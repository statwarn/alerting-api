package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.language.postfixOps

case class ActionCreate(
                         `type`: String,
                         webhook: ActionConfigurationModel
                         ) {
}

object ActionCreate {
  val jsonReads: Reads[ActionCreate] = (
    (__ \ "type").read[String] and
    (__ \ "webhook").read[ActionConfigurationModel]
  )(ActionCreate.apply _)

  val jsonWrites: Writes[ActionCreate] = (
    (__ \ "type").write[String] and
    (__ \ "webhook").write[ActionConfigurationModel]
  )(unlift(ActionCreate.unapply))

  implicit val jsonFormat: Format[ActionCreate] = Json.format[ActionCreate]
}
