package model.form

import model.form.data.{BlockPageFormData, SSHOrderFormData}
import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
object BlockPageForm {
  val form = Form(
    mapping(
      "page.url" -> nonEmptyText
    )(BlockPageFormData.apply)(BlockPageFormData.unapply)
  )
}
