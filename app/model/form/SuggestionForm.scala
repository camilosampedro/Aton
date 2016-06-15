package model.form

import model.form.data.{BlockPageFormData, SuggestionFormData}
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by camilosampedro on 10/05/16.
  */
object SuggestionForm {
  val form = Form(
    mapping(
      "suggestion.suggestionText" -> nonEmptyText
    )(SuggestionFormData.apply)(SuggestionFormData.unapply)
  )
}
