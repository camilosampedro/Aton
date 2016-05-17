package model.form

import model.form.data.ComputerFormPreData
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by camilo on 7/05/16.
  */
object ComputerFormPre {
  val form = Form(
    mapping(
      "computer.ip" -> nonEmptyText,
      "computer.SSHUser" -> nonEmptyText,
      "computer.SSHPassword" -> nonEmptyText
    )(ComputerFormPreData.apply)(ComputerFormPreData.unapply)
  )
}
