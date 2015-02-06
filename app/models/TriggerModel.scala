package models

import java.util.UUID

import anorm.~
import anorm.SqlParser.{get, str}
import org.joda.time.DateTime

import scala.language.postfixOps

case class TriggerModel(
                         trigger_id: UUID,
                         operator_id: String,
                         alert_id: UUID,
                         target_id: String,
                         operator_configuration: OperatorConfigurationModel,
                         createdAt: DateTime,
                         updatedAt: DateTime,
                         deletedAt: Option[DateTime]
                         ) {
}

object TriggerModel {
  val simple = get[UUID]("trigger_id") ~ str("operator_id") ~ get[UUID]("alert_id") ~ str("target_id") ~ OperatorConfigurationModel.simple("operator_configuration") ~
    get[DateTime]("createdAt") ~ get[DateTime]("updatedAt") ~ (get[DateTime]("deletedAt") ?) map {
    case trigger_id ~ operator_id ~ alert_id ~ target_id ~ operator_configuration ~ createdAt ~ updatedAt ~ deletedAt =>
      TriggerModel(trigger_id, operator_id, alert_id, target_id, operator_configuration, createdAt, updatedAt, deletedAt)
  }
}
