package model.form

import model.form.data.{BlockPageFormData, MessageFormData}
import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
object MessageForm {
  val form = Form(
    mapping(
      "message.message" -> nonEmptyText
    )(MessageFormData.apply)(MessageFormData.unapply)
  )
}
