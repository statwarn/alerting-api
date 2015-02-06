package models

import anorm.~
import anorm.SqlParser.{bool, str}

case class TargetModel(
                        id: String,
                        wildcard: Boolean
                        ) {
}

object TargetModel {
  val simple = str("id") ~ bool("wildcard") map {
    case id ~ wildcard =>
      TargetModel(id, wildcard)
  }
}
