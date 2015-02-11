package models

import java.util.UUID

import anorm.SqlParser.{get, str}
import anorm.~
import helpers.AnormCIText.rowToCIText
import helpers.CIText
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

import scala.language.postfixOps

case class TriggerModel(
                         trigger_id: UUID,
                         operator_id: String,
                         alert_id: UUID,
                         target_id: String,
                         target_value: String,
                         operator_configuration: OperatorConfigurationModel,
                         createdAt: DateTime,
                         updatedAt: DateTime,
                         deletedAt: Option[DateTime]
                         ) {
}

object TriggerModel {
  implicit val jsonFormat: Format[TriggerModel] = Json.format[TriggerModel]

  val simple = get[UUID]("trigger_id") ~ get[CIText]("operator_id") ~ get[UUID]("alert_id") ~ str("target_id") ~ str("target_value") ~
    OperatorConfigurationModel.simple("operator_configuration") ~
    get[DateTime]("createdAt") ~ get[DateTime]("updatedAt") ~ (get[DateTime]("deletedAt") ?) map {
    case trigger_id ~ operator_id ~ alert_id ~ target_id ~ target_value ~ operator_configuration ~ createdAt ~ updatedAt ~ deletedAt =>
      TriggerModel(trigger_id, operator_id, alert_id, target_id, target_value, operator_configuration, createdAt, updatedAt, deletedAt)
  }
}
