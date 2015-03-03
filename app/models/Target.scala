package models

object Target {
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
