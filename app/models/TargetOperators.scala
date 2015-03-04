package models

import play.api.libs.json.{Format, Json}

/**
 * Represents a target and the operators that can be applied to it,
 * for example "data.foo" with operators "equal", "threshold_min", "threshold_max", etc.
 * @param target Target data or metadata field (e.g. "data.foo")
 * @param operators Associated operators
 */
case class TargetOperators(target: String, operators: Seq[OperatorModel])

object TargetOperators {
  implicit val jsonFormat: Format[TargetOperators] = Json.format[TargetOperators]

  /**
   * Shortcut for calling Target(Target.substituteWildcard(..., ...), operators)
   * @param wildcardTargetId
   * @param targetField
   * @param operators
   * @return
   */
  def withWildcardTargetId(wildcardTargetId: String, targetField: String, operators: Seq[OperatorModel]): TargetOperators = {
    TargetOperators(TargetOperators.substituteWildcard(wildcardTargetId, targetField), operators)
  }

  def findTargetTypeForTargetValue(targetValue: String, targetTypes: Seq[TargetModel]): Option[TargetModel] = {
    targetTypes
      .find(_.target_id == targetValue) // First easy case, if the target value is "data" or "data.*"
      .orElse({
      // Return the "data.*" wildcard target model if the target value starts with "data.",
      // otherwise no match, the target value is invalid
      if (targetValue.startsWith("data.")) {
        targetTypes.find(_.wildcard)
      } else {
        None
      }
    })
  }

  /**
   * Substitute a wildcard target_id with the given field
   * @example
   * {{{
   *   Target.substituteWildcard("data.*", "foo")
   *   -> "data.foo"
   * }}}
   * @param target_id Target id containing the wildcard
   * @param field Field to substitute
   * @return
   */
  def substituteWildcard(target_id: String, field: String): String = {
    target_id.replace("*", field)
  }
}
