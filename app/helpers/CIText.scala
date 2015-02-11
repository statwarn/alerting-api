package helpers

import play.api.libs.json._

import scala.language.implicitConversions

case class CIText(text: String) {
}

object CIText {
  val jsonReads: Reads[CIText] = Reads({
    jsValue => JsSuccess(jsValue.toString())
  })

  val jsonWrites: Writes[CIText] = Writes({
    ciText => JsString(ciText)
  })

  implicit val jsonFormat: Format[CIText] = Format(jsonReads, jsonWrites)

  implicit def ciTextToString(ciText: CIText): String = ciText.text
  implicit def stringToCIText(text: String): CIText = CIText(text)
}
