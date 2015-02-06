package models

import anorm.~
import anorm.SqlParser.str
import play.api.libs.json.Json

case class OperatorModel(
                          id: String,
                          defaultConfiguration: OperatorConfigurationModel
                          ) {
}

object OperatorModel{
  val simple = str("id") ~ str("defaultConfiguration") map {
    case id ~ defaultConfiguration =>
      OperatorModel(id, Json.parse(defaultConfiguration).as[OperatorConfigurationModel])
  }
}
