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
      "room.name" -> nonEmptyText,
      "room.audiovisualResources" -> optional(text),
      "room.basicTools" -> optional(text),
      "room.laboratoryID" -> longNumber
    )(RoomFormData.apply)(RoomFormData.unapply)
  )
}
