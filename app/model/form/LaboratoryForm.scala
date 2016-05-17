package model.form

import model.form.data.LaboratoryFormData
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by camilosampedro on 10/05/16.
  */
object LaboratoryForm {
  val form = Form(
    mapping(
      "laboratory.name" -> nonEmptyText,
      "laboratory.location" -> optional(text),
      "laboratory.administration" -> optional(text)
    )(LaboratoryFormData.apply)(LaboratoryFormData.unapply)
  )
}
