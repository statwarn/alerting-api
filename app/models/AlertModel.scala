package models

import java.util.UUID

import anorm.~
import anorm.SqlParser.{bool, get, str}
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

import scala.language.postfixOps

case class AlertModel(
                       alert_id: UUID,
                       name: String,
                       createdAt: DateTime,
                       updatedAt: DateTime,
                       deletedAt: Option[DateTime],
                       activated: Boolean,
                       measurement_id: String
                       ) {
}

object AlertModel {
  implicit val jsonFormat: Format[AlertModel] = Json.format[AlertModel]

  val simple = get[UUID]("alert_id") ~ str("name") ~
    get[DateTime]("createdAt") ~ get[DateTime]("updatedAt") ~ (get[DateTime]("deletedAt") ?) ~
    bool("activated") ~ get[String]("measurement_id") map {
    case alert_id ~ name ~ createdAt ~ updatedAt ~ deletedAt ~ activated ~ measurement_id =>
      AlertModel(alert_id, name, createdAt, updatedAt, deletedAt, activated, measurement_id)
  }
}
