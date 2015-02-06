package models

import anorm.~
import anorm.SqlParser.str
import play.api.libs.json.Json

case class ActionModel(
                        action_id: String,
                        defaultConfiguration: ActionConfigurationModel
                        ) {
}

object ActionModel {
  val simple = str("action_id") ~ ActionConfigurationModel.simple("defaultConfiguration") map {
    case action_id ~ defaultConfiguration =>
      ActionModel(action_id, defaultConfiguration)
  }
}
