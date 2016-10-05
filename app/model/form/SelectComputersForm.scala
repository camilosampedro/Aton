package model.form

import model.form.data.{MessageFormData, SelectComputersFormData}
import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
object SelectComputersForm {
  val form = Form(
    mapping(
      "selectedcomputers" -> list(text)
    )(SelectComputersFormData.apply)(SelectComputersFormData.unapply)
  )
}
