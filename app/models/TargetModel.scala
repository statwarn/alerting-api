package models

import anorm.~
import anorm.SqlParser.{bool, str}
import play.api.libs.json.{Format, Json}

case class TargetModel(
                        target_id: String,
                        wildcard: Boolean
                        ) {
}

object TargetModel {
  implicit val jsonFormat: Format[TargetModel] = Json.format[TargetModel]

  val simple = str("target_id") ~ bool("wildcard") map {
    case target_id ~ wildcard =>
      TargetModel(target_id, wildcard)
  }
}
