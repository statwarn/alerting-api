package models

import java.sql.Connection
import java.util.UUID

import anorm.SqlParser.get
import anorm.{NamedParameter, SQL, SqlStringInterpolation}
import helpers.AnormUUID.uuidToStatement
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.DB

import scala.language.{implicitConversions, postfixOps}

object AlertRepository {
  /**
   * Enumeration describing the result when trying to delete an alert:
   * - Updated   => The alert has been found and deleted (its 'deletedAt' field has been set)
   * - Untouched => The alert has been found, but was already deleted (its 'deletedAt' field was already set)
   * - NotFound  => The alert has not been found
   */
  object DeleteResultStatus extends Enumeration {
    type DeleteResultStatus = Value
    val Updated, Untouched, NotFound = Value
  }

  /**
   * Retrieve all alerts from database
   * @return
   */
  def getAll(): Seq[Alert] = DB.withConnection {
    implicit connection =>
      getAlerts()
  }

  /**
   * Retrieve the specified alert from database
   * @param alertId UUID of the alert to retrieve
   * @return
   */
  def getById(alertId: UUID): Seq[Alert] = DB.withConnection {
    implicit connection =>
      getAlerts(alertId = Some(alertId), includeDeletedAlerts = true)
  }

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

  /**
   * Delete the alert with the given id.
   * @see [[models.AlertRepository.DeleteResultStatus]]
   * @param alertId UUID of the alert to delete
   * @return DeleteResultStatus describing the result of the attempt to delete the update
   */
  def delete(alertId: UUID): DeleteResultStatus.DeleteResultStatus = DB.withTransaction {
    implicit connection =>
      val deleteResultStatus = setAlertWithIdDeleted(alertId)

      if (deleteResultStatus == DeleteResultStatus.Updated) {
        // The alert was found and not already deleted
        setTriggersForAlertIdDeleted(alertId)
        setAlertActionsForAlertIdDeleted(alertId)
      }
      deleteResultStatus
  }

  // PRIVATE METHODS
  private def insertAlert(alertCreate: AlertCreate)(implicit connection: Connection): AlertModel = {
    SQL"""
          INSERT INTO alert (alert_id, name, "createdAt", "updatedAt", activated, measurement_id)
          VALUES (${alertCreate.alert_id}, ${alertCreate.name}, NOW(), NOW(), ${alertCreate.activated}, ${alertCreate.measurement_id})
          RETURNING *
       """.as(AlertModel.simple.single)
  }

  private def insertAlertAction(alertCreate: AlertCreate, actionCreate: ActionCreate)(implicit connection: Connection): AlertActionModel = {
    // Get the action type and its configuration (e.g.: { type: "webhook", webhook: { ... } })
    val actionType = actionCreate.`type`
    val actionConfiguration = actionCreate.webhook.configuration.toString()

    SQL"""
          INSERT INTO alert_action (alert_id, action_id, action_configuration, "createdAt", "updatedAt")
          VALUES (${alertCreate.alert_id}, $actionType, $actionConfiguration::JSONB, NOW(), NOW())
          RETURNING *
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
          VALUES (uuid_generate_v4(), $operatorName, ${alertCreate.alert_id}, ${targetModel.target_id}, ${triggerCreate.target}, $operatorConfiguration::JSONB, NOW(), NOW())
          RETURNING *
       """.as(TriggerModel.simple.single)
  }

  /**
   * Fetch alerts, triggers, and alert_actions from DB according to specified criteria, and build Alert instances
   * @param alertId If defined, will fetch only the alert with the specified id
   * @return
   */
  private def getAlerts(alertId: Option[UUID] = None, includeDeletedAlerts: Boolean = false)(implicit connection: Connection): Seq[Alert] = {
    // Retrieve instances of AlertModel, TriggerModel, AlertActionModel
    val alertModels = getAlertModels(alertId = alertId, includeDeletedAlerts = includeDeletedAlerts)
    val alertIds = alertModels.map(_.alert_id)

    // Group the triggers and actions using their alert_id
    val triggersByAlertId: Map[UUID, Seq[TriggerModel]] = getTriggerModelsForAlertIds(alertIds).groupBy(_.alert_id)
    val alertActionsByAlertId: Map[UUID, Seq[AlertActionModel]] = getAlertActionModelsForAlertIds(alertIds).groupBy(_.alert_id)

    // Build the Alert instances, using the AlertModel and its corresponding TriggerModel's and AlertActionModel's
    alertModels.map({
      alertModel => Alert(alertModel, triggersByAlertId.getOrElse(alertModel.alert_id, Nil), alertActionsByAlertId.getOrElse(alertModel.alert_id, Nil))
    })
  }

  /**
   * Fetch alerts from BD.
   * @param alertId If specified, fetch the alert with the given id
   * @param includeDeletedAlerts If specified, also include alerts with "deletedAt" set
   * @param connection SQL connection
   * @return
   */
  private def getAlertModels(alertId: Option[UUID] = None, includeDeletedAlerts: Boolean = false)(implicit connection: Connection): Seq[AlertModel] = {
    // Conditionally set which parameters will be passed to the prepared statement
    val simpleConditions: Seq[String] =
      Nil ++ (if (!includeDeletedAlerts) Seq("\"deletedAt\" IS NULL") else Nil)
    val namedParameters: Seq[(String, NamedParameter)] =
      Nil ++ alertId.map(uuid => Seq(("alert_id = {alertId}", NamedParameter("alertId", uuid)))).getOrElse(Nil)

    // Build the WHERE condition string using the above conditions
    val conditionsString = simpleConditions ++ namedParameters.map(_._1) match {
      case Nil => ""
      case conditions => "WHERE " + conditions.mkString(" AND ")
    }

    SQL(s"SELECT * FROM alert $conditionsString").on(namedParameters.map(_._2): _*).as(AlertModel.simple *)
  }

  private def getTriggerModelsForAlertIds(alertIds: Seq[UUID])(implicit connection: Connection): Seq[TriggerModel] = {
    alertIds match {
      case Nil => Nil
      case ids => SQL"""SELECT * FROM trigger WHERE alert_id IN ($alertIds)""".as(TriggerModel.simple *)
    }
  }

  private def getAlertActionModelsForAlertIds(alertIds: Seq[UUID])(implicit connection: Connection): Seq[AlertActionModel] = {
    alertIds match {
      case Nil => Nil
      case ids => SQL"""SELECT * FROM alert_action WHERE alert_id IN ($alertIds)""".as(AlertActionModel.simple *)
    }
  }

  /**
   * Find the alert with the given id and set its "deletedAt" to NOW().
   * @param alertId UUID of the alert to delete
   * @param connection SQL connection
   * @return If the alert was found and not already deleted, DeleteResultStatus.Updated.
   *         If the alert was found but already deleted, DeleteResultStatus.Untouched.
   *         If the alert was not found, DeleteResultStatus.NotFound.
   */
  private def setAlertWithIdDeleted(alertId: UUID)(implicit connection: Connection): DeleteResultStatus.DeleteResultStatus = {
    // Update the "deletedAt" for the row matching the given alertId, while retrieving the previous 'deletedAt' value
    val row: Option[Option[DateTime]] = SQL"""
            UPDATE alert AS after_update
            SET "deletedAt" = COALESCE(before_update."deletedAt", NOW())
            FROM alert AS before_update
            WHERE after_update.alert_id = $alertId AND after_update.alert_id = before_update.alert_id
            RETURNING before_update."deletedAt" AS "oldDeletedAt"
         """.as((get[DateTime]("oldDeletedAt") ?).singleOpt)

    row.map({
      // the 'deletedAt' column was already set, the alert was already deleted
      case Some(deletedAt) => DeleteResultStatus.Untouched
      // the 'deletedAt' column was not already set, the alert has just been deleted
      case None => DeleteResultStatus.Updated
    }).getOrElse(DeleteResultStatus.NotFound)
  }

  /**
   * Find the trigger with the given alert_id and set its "deletedAt" to NOW().
   * @param alertId Alert id of the trigger to delete
   * @param connection SQL connection
   * @return
   */
  private def setTriggersForAlertIdDeleted(alertId: UUID)(implicit connection: Connection): Unit = {
    SQL"""
          UPDATE trigger
          SET "deletedAt" = NOW()
          WHERE alert_id = $alertId
       """.executeUpdate()
  }

  /**
   * Find the alert action with the given alert_id and set its "deletedAt" to NOW().
   * @param alertId Alert id of the alert action to delete
   * @param connection SQL connection
   * @return
   */
  private def setAlertActionsForAlertIdDeleted(alertId: UUID)(implicit connection: Connection): Unit = {
    SQL"""
          UPDATE alert_action
          SET "deletedAt" = NOW()
          WHERE alert_id = $alertId
       """.executeUpdate()
  }
}
