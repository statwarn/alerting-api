package helpers

import java.sql.{SQLType, PreparedStatement}
import java.util.UUID

import anorm.ToStatement

object AnormUUID {
  /**
   * Proper handling of java.util.UUID type in SQL statements for PostgreSQL.
   * By default, anorm handles UUIDs like strings, for example: {{{SQL"""SELECT ... WHERE id = $myUUID"""}}}
   * triggers the following exception
   * {{{
   *   PSQLException: ERROR: operator does not exist: uuid = character varying
   *   Hint: No operator matches the given name and argument type(s). You might need to add explicit type casts.
   * }}}
   * Using this converter solves this problem:
   * {{{
   *   import helpers.AnormUUID.uuidToStatement
   *   SQL"""SELECT ... WHERE id = $myUUID"""
   *   // success
   * }}}
   * @return
   */
  implicit def uuidToStatement: ToStatement[UUID] = new ToStatement[UUID] {
    def set(statement: PreparedStatement, i: Int, value: UUID): Unit = {
      statement.setObject(i, value)
    }
  }
}
