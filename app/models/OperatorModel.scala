package models

import anorm.SqlParser.get
import anorm.~
import helpers.AnormCIText.rowToCIText
import helpers.CIText
import play.api.libs.json.Json

import scala.language.postfixOps

case class OperatorModel(
                          operator_id: String,
                          defaultConfiguration: OperatorConfigurationModel
                          ) {
}

object OperatorModel {
  implicit val jsonFormat = Json.format[OperatorModel]

  /**
   * Simple Anorm RowParser to extract all fields from the 'operator' table
   */
  val simple = get[CIText]("operator_id") ~ OperatorConfigurationModel.simple("defaultConfiguration") map {
    case operator_id ~ defaultConfiguration =>
      OperatorModel(operator_id, defaultConfiguration)
  }
}
