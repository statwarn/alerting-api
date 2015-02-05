package models

import java.util.UUID

import org.joda.time.DateTime

case class AlertActionModel(
                             alert_id: UUID,
                             action_id: String,
                             configuration: ActionConfigurationModel,
                             createdAt: DateTime,
                             updatedAt: DateTime,
                             deletedAt: Option[DateTime]
                             ) {
}
