package models

import anorm.SqlParser.get
import helpers.AnormJsValue.rowToJsValue
import play.api.libs.json._

/**
 * Contains the configuration of an action.
 * For now, the configuration can contain any js object, but it should later
 * be defined more strictly
 * @param configuration The action's configuration
 */
case class ActionConfigurationModel(configuration: JsObject) {
}

object ActionConfigurationModel {
  /**
   * Transforms a JsValue into JsSuccess[ActionConfigurationModel] or JsError
   */
  val jsonReads: Reads[ActionConfigurationModel] = Reads(jsValue => JsSuccess(ActionConfigurationModel(jsValue.as[JsObject])))

  /**
   * Transforms an instance of ActionConfigurationModel into a JsValue
   */
  val jsonWrites: Writes[ActionConfigurationModel] = Writes(actionConfigurationModel => actionConfigurationModel.configuration)

  implicit val jsonFormat: Format[ActionConfigurationModel] = Format(jsonReads, jsonWrites)

  /**
   * Anorm RowParser to extract the action configuration stored as JSON in PostgreSQL and transform
   * it into an ActionConfigurationModel
   * @param configurationColumn Name of the SQL column to extract
   * @return
   */
  def simple(configurationColumn: String) = get[JsValue](configurationColumn) map {
    case configuration =>
      ActionConfigurationModel(configuration.as[JsObject])
  }
}
