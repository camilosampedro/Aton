package model.form

import model.form.data.ComputerFormPostData
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by camilo on 7/05/16.
  */
object ComputerFormPost {
  val form = Form(
    mapping(
      "Dirección IP" -> nonEmptyText,
      "Nombre" -> nonEmptyText,
      "Usuario SSH" -> nonEmptyText,
      "Password SSH" -> nonEmptyText,
      "Description" -> nonEmptyText,
      "Sala" -> longNumber
    )(ComputerFormPostData.apply)(ComputerFormPostData.unapply)
  )
}
