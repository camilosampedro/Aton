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
      "sshorder.superuser" -> boolean,
      "sshorder.command" -> nonEmptyText
    )(SSHOrderFormData.apply)(SSHOrderFormData.unapply)
  )
}
