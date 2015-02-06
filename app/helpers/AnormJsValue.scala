package helpers

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}
import play.api.libs.json.{Json, JsValue}

/**
 * Helper containing functions to manipulate JsValue with Anorm
 */
object AnormJsValue {
  /**
   * Implicit extractor to transform a PostgreSQL json/jsonb record into a JsValue
   * Example:
   * {{{
   *   import anorm.SqlParser.get
   *   import anorm.SqlStringInterpolation
   *   import helpers.AnormJsValue.rowToJsValue
   *   ...
   *   SQL"SELECT json FROM ...".as(get[JsValue]("json").single)
   * }}}
   *
   * Source: http://www.philmateleven.com/2013/01/using-postgresqls-native-json-support.html
   *
   * @return
   */
  implicit def rowToJsValue: Column[JsValue] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case pgo: org.postgresql.util.PGobject => Right(Json.parse(pgo.getValue))
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" +
        value.asInstanceOf[AnyRef].getClass + " to JsValue for column " + qualified))
    }
  }
}
