package controllers

import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

/**
  * Created by camilo on 14/05/16.
  */
class SuggestionController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def home = Action {
    NotImplemented(views.html.index(Some("Invitado"), true, "Suggestion")(views.html.notImplemented(messagesApi("suggestion.notImplemented"))))
  }
}
