package model.form

import model.form.data.SSHOrderFormData
import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
object SSHOrderForm {
  val form = Form(
    mapping(
      "sshorder.superuser" -> boolean,
      "sshorder.command" -> nonEmptyText
    )(SSHOrderFormData.apply)(SSHOrderFormData.unapply)
  )
}
