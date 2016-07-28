package model.form

import model.form.data.ComputerFormData
import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
object ComputerForm {
  val form = Form(
    mapping(
      "computer.ip" -> nonEmptyText,
      "room.name" -> optional(text),
      "computer.SSHUser" -> nonEmptyText,
      "computer.SSHPassword" -> nonEmptyText,
      "description" -> optional(text),
      "roomID" -> optional(longNumber)
    )(ComputerFormData.apply)(ComputerFormData.unapply)
  )
}
