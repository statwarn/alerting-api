package models

import java.sql.Connection

import anorm.SqlStringInterpolation
import play.api.Play.current
import play.api.db.DB

import scala.language.postfixOps

object ActionRepository {
  def getAll(): Seq[ActionModel] = DB.withConnection {
    implicit connection => getActions()
  }

  //
  // Private methods
  //

  private def getActions()(implicit connection: Connection): Seq[ActionModel] = {
    SQL"""SELECT * FROM action""".as(ActionModel.simple *)
  }
}
