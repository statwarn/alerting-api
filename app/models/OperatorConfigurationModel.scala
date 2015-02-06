package models

import anorm.SqlParser.get
import helpers.AnormJsValue.rowToJsValue
import play.api.libs.json._

/**
 * Contains the configuration of an operator.
 * For now, the configuration can contain any js object, but it should later
 * be defined more strictly
 * @param configuration The operator's configuration
 */
case class OperatorConfigurationModel(configuration: JsObject) {
}

object OperatorConfigurationModel {
  /**
   * Transforms a JsValue into JsSuccess[OperatorConfigurationModel] or JsError
   */
  val jsonReads: Reads[OperatorConfigurationModel] = Reads(jsValue => JsSuccess(OperatorConfigurationModel(jsValue.as[JsObject])))

  /**
   * Transforms an instance of OperatorConfigurationModel into a JsValue
   */
  val jsonWrites: Writes[OperatorConfigurationModel] = Writes(operatorConfigurationModel => operatorConfigurationModel.configuration)

  implicit val jsonFormat: Format[OperatorConfigurationModel] = Format(jsonReads, jsonWrites)

  /**
   * Anorm RowParser to extract the operator configuration stored as JSON in PostgreSQL and transform
   * it into an OperatorConfigurationModel
   * @param configurationColumn Name of the SQL column to extract
   * @return
   */
  def simple(configurationColumn: String) = get[JsValue](configurationColumn) map {
    case configuration =>
      OperatorConfigurationModel(configuration.as[JsObject])
  }
}
