package model.form

import model.form.data.{MessageFormData, SelectComputersFormData}
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by camilosampedro on 10/05/16.
  */
object SelectComputersForm {
  val form = Form(
    mapping(
      "selectedcomputers" -> list(text)
    )(SelectComputersFormData.apply)(SelectComputersFormData.unapply)
  )
}
