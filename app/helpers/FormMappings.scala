package helpers

import java.util.UUID

import play.api.data.{Forms, Mapping, FormError}
import play.api.data.format.Formatter
import play.api.libs.json.{Json, JsObject}

import scala.util.{Failure, Success, Try}

object FormMappings {
  implicit def uuidFormatter: Formatter[UUID] = new Formatter[UUID] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], UUID] = {
      Try(UUID.fromString(data(key))) match {
        case Success(uuid) => Right(uuid)
        case Failure(ex) => Left(Seq(FormError(key, s"Cannot parse $key as UUID")))
      }
    }

    override def unbind(key: String, value: UUID): Map[String, String] = {
      Map(key -> value.toString)
    }
  }

  val uuidMapping: Mapping[UUID] = Forms.of[UUID]

  implicit def jsObjectFormatter: Formatter[JsObject] = new Formatter[JsObject] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], JsObject] = {
      println(s"data.$key -> ${Json.toJson(data.filterKeys(_.startsWith(key)))}")

      data.get(key).flatMap(s => Json.parse(s).asOpt[JsObject]).toRight(Seq[FormError](FormError(key, s"Cannot parse $key as JsObject")))
    }

    override def unbind(key: String, value: JsObject): Map[String, String] = {
      Map(key -> value.toString())
    }
  }

  val jsObjectMapping: Mapping[JsObject] = Forms.of[JsObject]
}
