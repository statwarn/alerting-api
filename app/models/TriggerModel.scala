package models

import java.util.UUID

import anorm.~
import anorm.SqlParser.{get, str}
import org.joda.time.DateTime

import scala.language.postfixOps

case class TriggerModel(
                         id: UUID,
                         operator_id: String,
                         alert_id: UUID,
                         target_id: String,
                         configuration: OperatorConfigurationModel,
                         createdAt: DateTime,
                         updatedAt: DateTime,
                         deletedAt: Option[DateTime]
                         ) {
}

object TriggerModel {
  val simple = get[UUID]("id") ~ str("operator_id") ~ get[UUID]("alert_id") ~ str("target_id") ~ OperatorConfigurationModel.simple("configuration") ~
    get[DateTime]("createdAt") ~ get[DateTime]("updatedAt") ~ (get[DateTime]("deletedAt") ?) map {
    case id ~ operator_id ~ alert_id ~ target_id ~ configuration ~ createdAt ~ updatedAt ~ deletedAt =>
      TriggerModel(id, operator_id, alert_id, target_id, configuration, createdAt, updatedAt, deletedAt)
  }
}
