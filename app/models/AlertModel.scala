package models

import java.util.UUID

import anorm.~
import anorm.SqlParser.{bool, get, str}
import org.joda.time.DateTime

import scala.language.postfixOps

case class AlertModel(
                       id: UUID,
                       name: String,
                       createdAt: DateTime,
                       updatedAt: DateTime,
                       deletedAt: Option[DateTime],
                       activated: Boolean,
                       measurement_id: String
                       ) {
}

object AlertModel {
  val simple = get[UUID]("id") ~ str("name") ~
    get[DateTime]("createdAt") ~ get[DateTime]("updatedAt") ~ (get[DateTime]("deletedAt") ?) ~
    bool("activated") ~ str("measurement_id") map {
    case id ~ name ~ createdAt ~ updatedAt ~ deletedAt ~ activated ~ measurement_id =>
      AlertModel(id, name, createdAt, updatedAt, deletedAt, activated, measurement_id)
  }
}
