package models

import java.util.UUID

import anorm.~
import anorm.SqlParser.{get, str}
import org.joda.time.DateTime

import scala.language.postfixOps

import play.api.libs.json.Json

case class AlertActionModel(
                             alert_id: UUID,
                             action_id: String,
                             configuration: ActionConfigurationModel,
                             createdAt: DateTime,
                             updatedAt: DateTime,
                             deletedAt: Option[DateTime]
                             ) {
}

object AlertActionModel {
  val simple = get[UUID]("alert_id") ~ str("action_id") ~ str("configuration") ~
    get[DateTime]("createdAt") ~ get[DateTime]("updatedAt") ~ (get[DateTime]("deletedAt") ?) map {
    case alert_id ~ action_id ~ configuration ~ createdAt ~ updatedAt ~ deletedAt =>
      AlertActionModel(alert_id, action_id, Json.parse(configuration).as[ActionConfigurationModel], createdAt, updatedAt, deletedAt)
  }
}
