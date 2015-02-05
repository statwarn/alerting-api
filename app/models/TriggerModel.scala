package models

import java.util.UUID

import org.joda.time.DateTime

case class TriggerModel(
                         id: UUID,
                         operator_id: String,
                         alert_id: UUID,
                         target_id: String,
                         configuration: OperatorConfigurationModel,
                         createdAt: DateTime,
                         updatedAt: DateTime,
                         deletedAt: Option[DateTime]
                         ) {
}
