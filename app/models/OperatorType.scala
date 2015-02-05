package models

object OperatorType extends Enumeration {
  type OperatorType = Value
  val NoData, AnyData, Anomalous, ThresholdMin, ThresholdMax, Equal, NewAttribute = Value
}
