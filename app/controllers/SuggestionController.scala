package controllers

import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html._

/**
  * Created by camilo on 14/05/16.
  */
class SuggestionController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def home = Action {
    implicit val username=None
    implicit val isAdmin=false
    NotImplemented(index(messagesApi("suggestion"),notImplemented(messagesApi("suggestion.notImplemented"))))
  }
}
