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
  implicit val errorWrites: Writes[(String, String)] = new Writes[(String, String)] {
    override def writes(error: (String, String)): JsObject = Json.obj(
      "key" -> error._1,
      "extra" -> error._2
    )
  }
  implicit val resultMessageWrites: Writes[ResultMessage] = new Writes[ResultMessage] {
    override def writes(o: ResultMessage) = Json.obj(
      "result" -> o.result,
      "extras" -> o.extras.map(Json.toJson(_))
    )
  }


}
