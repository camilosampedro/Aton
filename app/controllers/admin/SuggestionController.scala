package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{SuggestionDAO, UserDAO}
import play.api.Environment
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext

/**
  * Created by camilosampedro on 11/05/16.
  */
class SuggestionController @Inject()(suggestionDAO: SuggestionDAO, val messagesApi: MessagesApi)(implicit userDAO: UserDAO, executionContext: ExecutionContext , environment: Environment) extends ControllerWithAuthRequired {
  def delete(id: Long) = AuthRequiredAction { implicit request =>
    suggestionDAO.delete(id).map { res =>
      Redirect(normalroutes.SuggestionController.home())
    }
  }
}
