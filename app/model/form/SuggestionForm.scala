package model.form

import model.form.data.{BlockPageFormData, SuggestionFormData}
import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
object SuggestionForm {
  val form = Form(
    mapping(
      "suggestion.suggestionText" -> nonEmptyText
    )(SuggestionFormData.apply)(SuggestionFormData.unapply)
  )
}
