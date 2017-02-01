package model.form

import model.form.data.LaboratoryFormData
import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
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
