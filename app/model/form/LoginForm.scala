package model.form

import model.form.data.LoginFormData
import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
object LoginForm {
  val form = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginFormData.apply)(LoginFormData.unapply)
  )
}
