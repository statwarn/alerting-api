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
}
