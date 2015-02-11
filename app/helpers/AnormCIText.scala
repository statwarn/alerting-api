package helpers

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

object AnormCIText {
  implicit def rowToCIText: Column[CIText] = Column.nonNull1 { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case pgo: org.postgresql.util.PGobject => Right(pgo.toString)
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" +
        value.asInstanceOf[AnyRef].getClass + " to CIText for column " + qualified))
    }
  }
}
