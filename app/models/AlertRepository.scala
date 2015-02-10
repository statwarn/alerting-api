package models

import java.sql.Connection

import anorm.SqlStringInterpolation
import play.api.db.DB
import play.api.Play.current

import scala.language.postfixOps

object AlertRepository {
  def getAll(): Seq[AlertModel] = ???

  def getById(): Seq[AlertModel] = ???

  /**
   * Extract the given alert data and save the alert in DB by inserting the appropriate rows in alert, alert_action and trigger,
   * and finally return the created models
   * @param alertCreate Alert to create
   * @return
   */
  def create(alertCreate: AlertCreate) = DB.withTransaction {
    implicit connection =>
      val alertModel = insertAlert(alertCreate)
      val alertActionModels = alertCreate.actions.map(actionCreate => insertAlertAction(alertCreate, actionCreate))
      val possibleTargets: Seq[TargetModel] = getAllTargets()
      val triggerModels: Seq[TriggerModel] = alertCreate.triggers.map(triggerCreate => insertTrigger(alertCreate, triggerCreate, possibleTargets))

      Alert(alertModel, triggerModels, alertActionModels)
  }

  def update(alert: Alert): Boolean = ???

  def delete(alert: AlertModel): Boolean = ???

  // PRIVATE METHODS
  private def insertAlert(alertCreate: AlertCreate)(implicit connection: Connection): AlertModel = {
    SQL"""
          INSERT INTO alert (alert_id, name, "createdAt", "updatedAt", activated, measurement_id)
          VALUES (${alertCreate.alert_id}::UUID, ${alertCreate.name}, NOW(), NOW(), ${alertCreate.activated}, ${alertCreate.measurement_id}::UUID)
          RETURNING *
       """.as(AlertModel.simple.single)
  }

  private def insertAlertAction(alertCreate: AlertCreate, actionCreate: ActionCreate)(implicit connection: Connection): AlertActionModel = {
    // Get the action type and its configuration (e.g.: { type: "webhook", webhook: { ... } })
    val actionType = actionCreate.`type`
    val actionConfiguration = actionCreate.webhook.configuration.toString()

    SQL"""
          INSERT INTO alert_action (alert_id, action_id, action_configuration, "createdAt", "updatedAt")
          VALUES (${alertCreate.alert_id}::UUID, $actionType, $actionConfiguration::JSONB, NOW(), NOW())
          RETURNING *, action_id::text
       """.as(AlertActionModel.simple.single)
  }

  private def getAllTargets()(implicit connection: Connection): Seq[TargetModel] = {
    SQL"""SELECT * FROM target""".as(TargetModel.simple +)
  }

  private def insertTrigger(alertCreate: AlertCreate, triggerCreate: TriggerCreate, possibleTargets: Seq[TargetModel])(implicit connection: Connection): TriggerModel = {
    // Get the operator name, and the configuration (removing the "name" field from the configuration)
    val operatorName = (triggerCreate.operator \ "name").as[String]
    val operatorConfiguration = (triggerCreate.operator - "name").toString()

    // Find which target corresponds to the given target value, if no target is found, an exception is raised and the transaction rolled back
    val targetModel = Target.findTargetTypeForTargetValue(triggerCreate.target, possibleTargets).ensuring(_.isDefined, s"Invalid target value ${triggerCreate.target}").get

    SQL"""
          INSERT INTO trigger (trigger_id, operator_id, alert_id, target_id, target_value, operator_configuration, "createdAt", "updatedAt")
          VALUES (uuid_generate_v4(), $operatorName, ${alertCreate.alert_id}::UUID, ${targetModel.target_id}, ${triggerCreate.target}, $operatorConfiguration::JSONB, NOW(), NOW())
          RETURNING *, operator_id::TEXT, target_id::TEXT
       """.as(TriggerModel.simple.single)
  }
}
