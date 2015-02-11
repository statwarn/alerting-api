package models

import java.util.UUID

import anorm.SqlParser.get
import anorm.~
import helpers.AnormCIText.rowToCIText
import helpers.CIText
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

import scala.language.postfixOps

case class AlertActionModel(
                             alert_id: UUID,
                             action_id: String,
                             action_configuration: ActionConfigurationModel,
                             createdAt: DateTime,
                             updatedAt: DateTime,
                             deletedAt: Option[DateTime]
                             ) {
}

object AlertActionModel {
  implicit val jsonFormat: Format[AlertActionModel] = Json.format[AlertActionModel]

  val simple = get[UUID]("alert_id") ~ get[CIText]("action_id") ~ ActionConfigurationModel.simple("action_configuration") ~
    get[DateTime]("createdAt") ~ get[DateTime]("updatedAt") ~ (get[DateTime]("deletedAt") ?) map {
    case alert_id ~ action_id ~ action_configuration ~ createdAt ~ updatedAt ~ deletedAt =>
      AlertActionModel(alert_id, action_id, action_configuration, createdAt, updatedAt, deletedAt)
  }
}
