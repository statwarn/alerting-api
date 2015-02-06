package models

import anorm.~
import anorm.SqlParser.{bool, str}

case class TargetModel(
                        target_id: String,
                        wildcard: Boolean
                        ) {
}

object TargetModel {
  val simple = str("target_id") ~ bool("wildcard") map {
    case target_id ~ wildcard =>
      TargetModel(target_id, wildcard)
  }
}
