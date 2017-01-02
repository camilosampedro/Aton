package model.json

/**
  * Created by camilosampedro on 5/11/16.
  */
case class ResultMessage(
                          result: String,
                          extra: Seq[String]
                        ){
  def this(result: String) = this(result, Seq.empty)
}

