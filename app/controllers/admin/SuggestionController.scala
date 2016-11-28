package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import play.api.Environment
import play.api.i18n.MessagesApi
import services.{SuggestionService, UserService, state}

import scala.concurrent.ExecutionContext

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class SuggestionController @Inject()(suggestionService: SuggestionService, val messagesApi: MessagesApi)(implicit userService: UserService, executionContext: ExecutionContext, environment: Environment) extends ControllerWithAuthRequired {
  def delete(id: Long) = AuthRequiredAction { implicit request =>
    suggestionService.delete(id).map {
      case state.ActionCompleted => Redirect(normalroutes.SuggestionController.home())
      case _ => BadRequest
    }
  }
}
