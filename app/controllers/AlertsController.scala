package controllers

import java.util.UUID

import play.api.mvc._

object AlertsController extends Controller {
  /**
   * Create an alert
   * @return
   */
  def create = Action {
    implicit request => ???
  }

  /**
   * Update an alert
   * @param alertId Id of the alert to update
   * @return
   */
  def update(alertId: UUID) = Action {
    implicit request => ???
  }

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
   *
   * @param alertId Id of the alert to delete
   * @return
   */
  def delete(alertId: UUID) = Action {
    implicit request => ???
  }
}
