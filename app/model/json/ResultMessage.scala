package model.json

import play.api.data.validation.ValidationError
import play.api.libs.json.JsPath

/**
  * Created by camilosampedro on 5/11/16.
  */
case class ResultMessage(
                          result: String,
                          extras: Seq[ResultMessageExtra]
                        ){
  def this(result: String) = this(result, Seq.empty)
}

object ResultMessage {
  val inputWasNotAJson: ResultMessage = new ResultMessage("Input was not a JSON")
  def wrongJsonFormat(errors: Seq[(JsPath, Seq[ValidationError])]): ResultMessage = {
    val mapped = errors.map(singleError=>{
      val error = singleError._2.map(y => y.message match {
        case "error.path.missing" => "Missing"
        case "error.expected.jsnumber" => "Number expected"
        case otherError => otherError
      }).mkString(", ")
      ResultMessageExtra(singleError._1.toString().drop(1),error)
    })
    ResultMessage("Wrong json format", mapped)
  }
}

