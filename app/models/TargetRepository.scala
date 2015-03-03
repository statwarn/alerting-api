package models

import java.sql.Connection

import anorm.SqlParser.flatten
import anorm.SqlStringInterpolation
import play.api.Play.current
import play.api.db.DB

import scala.language.postfixOps

object TargetRepository {
  /**
   * Get the possible targets and operators for the given data fields
   * @param dataFields Data fields (e.g. "foo", "bar")
   * @return
   */
  def getByTargetFields(dataFields: Seq[String]): Seq[Target] = DB.withConnection {
    implicit connection =>
      getTargetsAndOperators() map {
        // If the target is a wildcard, associate that target for all given dataFields
        case (TargetModel(target_id, true), operator) =>
          dataFields.map(dataField => Target.withWildcardTargetId(target_id, dataField, operator))

        // If the target is not a wildcard (e.g. "data"), use it as-is
        case (TargetModel(target_id, false), operator) =>
          Seq(Target(target_id, operator))
      } flatten
  }

  //
  // Private methods
  //

  /**
   * Get the possible combinations of targets and operators
   * @param connection SQL connection
   * @return
   */
  def getTargetsAndOperators()(implicit connection: Connection): Seq[(TargetModel, OperatorModel)] = {
    SQL"""
          SELECT * FROM target
          JOIN operator_target USING(target_id)
          JOIN operator USING(operator_id)
       """ as(((TargetModel.simple ~ OperatorModel.simple) map flatten) *)
  }
}
