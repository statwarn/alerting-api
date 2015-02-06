package models

import anorm.~
import anorm.SqlParser.str

case class OperatorTargetModel(
                                operator_id: String,
                                target_id: String
                                ) {
}

object OperatorTargetModel {
  val simple = str("operator_id") ~ str("target_id") map {
    case operator_id ~ target_id =>
      OperatorTargetModel(operator_id, target_id)
  }
}