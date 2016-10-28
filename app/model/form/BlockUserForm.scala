package model.form

import model.form.data.BlockUserFormData
import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Benjamin R. White <ben@delt.as>
  */
object BlockUserForm {
  val form = Form(
    mapping(
      "blockUser.username" -> nonEmptyText
    )(BlockUserFormData.apply)(BlockUserFormData.unapply)
  )
}
