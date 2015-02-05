package models

import models.ActionType.ActionType

case class ActionModel(
                        id: ActionType,
                        defaultConfiguration: ActionConfigurationModel
                        ) {
}
