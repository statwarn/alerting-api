package models

import java.sql.Connection
import java.util.UUID

import anorm.{ParameterValue, NamedParameter, SqlStringInterpolation, SQL}
import helpers.AnormUUID.uuidToStatement
import play.api.db.DB
import play.api.Play.current

import scala.language.{implicitConversions, postfixOps}

object AlertRepository {
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
      getAlerts(Some(alertId))
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

  def delete(alert: AlertModel): Boolean = ???

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
   * Simple helper used to define dynamic queries, based on criteria.
   * @param criteria Conditional part of the query, to avoid outputting "None" when not defined
   * @return
   */
  private def opt(criteria: Option[String]): String = {
    criteria.getOrElse("")
  }

  /**
   * Fetch alerts from DB according to specified criteria
   * @param alertId If defined, will fetch only the alert with the specified id
   * @return
   */
  private def getAlerts(alertId: Option[UUID] = None)(implicit connection: Connection): Seq[Alert] = {
    def getAlertModels(alertId: Option[UUID]): Seq[AlertModel] = {
      // Conditionally set which parameters will be passed to the prepared statement
      val namedParameters = alertId.map(uuid => Seq(NamedParameter("alertId", uuid))).getOrElse(Nil)

      SQL(
        s"""
           |SELECT * FROM alert
           |${opt(alertId.map(_ => s"WHERE alert_id = {alertId}"))}
       """.stripMargin
      ).on(namedParameters: _*).as(AlertModel.simple *)
    }

    // Retrieve instances of AlertModel, TriggerModel, AlertActionModel
    val alertModels = getAlertModels(alertId)
    val triggerModels = SQL"""SELECT * FROM trigger WHERE alert_id IN (${alertModels.map(_.alert_id)})""".as(TriggerModel.simple *)
    val alertActionModels = SQL"""SELECT * FROM alert_action WHERE alert_id IN (${alertModels.map(_.alert_id)})""".as(AlertActionModel.simple *)

    // Group the triggers and actions using their alert_id
    val triggersByAlertId: Map[UUID, List[TriggerModel]] = triggerModels.groupBy(_.alert_id)
    val alertActionsByAlertId: Map[UUID, List[AlertActionModel]] = alertActionModels.groupBy(_.alert_id)

    // Build the Alert instances, using the AlertModel and its corresponding TriggerModel's and AlertActionModel's
    alertModels.map({
      alertModel => Alert(alertModel, triggersByAlertId.getOrElse(alertModel.alert_id, Nil), alertActionsByAlertId.getOrElse(alertModel.alert_id, Nil))
    })
  }
}