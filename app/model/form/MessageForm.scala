package model.form

import model.form.data.{BlockPageFormData, MessageFormData}
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by camilosampedro on 10/05/16.
  */
object MessageForm {
  val form = Form(
    mapping(
      "message.message" -> nonEmptyText
    )(MessageFormData.apply)(MessageFormData.unapply)
  )
}
