package model

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by camilosampedro on 10/12/16.
  */
package object json {



  implicit val loginJsonReads: Reads[LoginJson] = Json.reads[LoginJson]
  implicit val computerJsonWrites: Writes[ComputerJson] = Json.writes[ComputerJson]
  implicit val computerJsonReads: Reads[ComputerJson] = Json.reads[ComputerJson]
  implicit val laboratoryWrites: Writes[LaboratoryJson] = Json.writes[LaboratoryJson]
  implicit val laboratoryReads: Reads[LaboratoryJson] = Json.reads[LaboratoryJson]
  implicit lazy val resultMessageReads: Reads[ResultMessage] = (
    (JsPath \ "result").read[String] and
      (JsPath \ "errors").read[Seq[String]]
    )(ResultMessage.apply _)
  implicit val resultMessageWrites = new Writes[ResultMessage] {
    def writes(resultMessage: ResultMessage): JsObject = Json.obj (
      "result" -> resultMessage.result,
      "errors" -> resultMessage.extra
    )
  }


}
