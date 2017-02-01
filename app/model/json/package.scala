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
  implicit val extrasWrites: Writes[ResultMessageExtra] = Json.writes[ResultMessageExtra]
  implicit val resultMessageWrites: Writes[ResultMessage] = new Writes[ResultMessage] {
    override def writes(o: ResultMessage): JsObject = Json.obj(
      "result" -> o.result,
      "extras" -> o.extras.map(Json.toJson(_))
    )
  }
  implicit val ResultMessageExtraF: Format[ResultMessageExtra] = Json.format[ResultMessageExtra]
  implicit val resultMessageReads: Reads[ResultMessage] = (
    (__ \ "result").read[String] and
    (__ \ "extras").read[Seq[ResultMessageExtra]]
  )(ResultMessage.apply _)

  implicit val sshOrderJsonReads: Reads[SSHOrderJson] = Json.reads[SSHOrderJson]

  implicit def convertToExtra(raw: (String, String)): ResultMessageExtra = ResultMessageExtra(raw._1, raw._2)
}
