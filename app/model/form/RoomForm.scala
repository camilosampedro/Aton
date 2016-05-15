package model.form

import model.form.data.RoomFormData
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by camilosampedro on 10/05/16.
  */
object RoomForm {
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "audiovisualResources" -> optional(text),
      "basicTools" -> optional(text),
      "laboratoryID" -> longNumber
    )(RoomFormData.apply)(RoomFormData.unapply)
  )
}
