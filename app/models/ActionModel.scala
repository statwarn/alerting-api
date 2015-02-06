package models

import anorm.~
import anorm.SqlParser.str
import play.api.libs.json.Json

case class ActionModel(
                        id: String,
                        defaultConfiguration: ActionConfigurationModel
                        ) {
}

object ActionModel {
  val simple = str("id") ~ str("defaultConfiguration") map {
    case id ~ defaultConfiguration =>
      ActionModel(id, Json.parse(defaultConfiguration).as[ActionConfigurationModel])
  }
}
