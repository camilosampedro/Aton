package model.form

import model.form.data.{BlockPageFormData, SSHOrderFormData}
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by camilosampedro on 10/05/16.
  */
object BlockPageForm {
  val form = Form(
    mapping(
      "page.url" -> nonEmptyText
    )(BlockPageFormData.apply)(BlockPageFormData.unapply)
  )
}
