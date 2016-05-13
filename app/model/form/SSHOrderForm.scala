package model.form

import model.form.data.SSHOrderFormData
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by camilosampedro on 10/05/16.
  */
object SSHOrderForm {
  val form = Form(
    mapping(
      "superUser" -> boolean,
      "interrupt" -> boolean,
      "command" -> nonEmptyText
    )(SSHOrderFormData.apply)(SSHOrderFormData.unapply)
  )
}
