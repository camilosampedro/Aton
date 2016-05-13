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
      "ip" -> nonEmptyText,
      "SSHUser" -> nonEmptyText,
      "SSHPassword" -> nonEmptyText
    )(ComputerFormPreData.apply)(ComputerFormPreData.unapply)
  )
}
