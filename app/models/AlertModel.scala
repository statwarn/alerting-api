package models

import java.util.UUID

import org.joda.time.DateTime

case class AlertModel(
                       id: UUID,
                       name: String,
                       createdAt: DateTime,
                       updatedAt: DateTime,
                       deletedAt: Option[DateTime],
                       activated: Boolean,
                       measurement_id: String
                       ) {
}
