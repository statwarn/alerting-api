package models

import models.OperatorType.OperatorType

case class OperatorModel(
                          id: OperatorType,
                          defaultConfiguration: OperatorConfigurationModel
                          ) {
}
