package model.json

import model.ResultMessage
import play.api.libs.json.{Reads, JsPath}
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by camilosampedro on 5/11/16.
  */
object ModelReads {
  implicit lazy val resultMessageReads: Reads[ResultMessage] = (
      (JsPath \ "result").read[String] and
      (JsPath \ "errors").read[Seq[String]]
    )(ResultMessage.apply _)
}
