package model.form

import model.form.data.ComputerFormData
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by camilo on 7/05/16.
  */
object ComputerForm {
  val form = Form(
    mapping(
      "ip" -> nonEmptyText,
      "name" -> nonEmptyText,
      "SSHUser" -> nonEmptyText,
      "SSHPassword" -> nonEmptyText,
      "description" -> nonEmptyText,
      "roomID" -> longNumber
    )(ComputerFormData.apply)(ComputerFormData.unapply)
  )
}
