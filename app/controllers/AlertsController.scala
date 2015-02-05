package controllers

import java.util.UUID

import play.api.mvc._

object AlertsController extends Controller {
  /**
   * Get all alerts
   * @return
   */
  def getAll = Action {
    implicit request => ???
  }

  /**
   * Get the alert with the given id
   * @param alertId Id of the alert to retrieve
   * @return
   */
  def getById(alertId: UUID) = Action {
    implicit request => ???
  }

  /**
   * Create or update an alert
   * @param alertId Id of the alert to create/update
   * @return
   */
  def createOrUpdate(alertId: UUID) = Action {
    implicit request => ???
  }

  /**
   *
   * @param alertId Id of the alert to delete
   * @return
   */
  def delete(alertId: UUID) = Action {
    implicit request => ???
  }
}
