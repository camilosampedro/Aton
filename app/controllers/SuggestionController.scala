package controllers

import java.sql.Timestamp
import java.util.Calendar

import com.google.inject.Inject
import model.form.SuggestionForm
import model.{Role, Suggestion}
import play.api.Environment
import play.api.i18n.MessagesApi
import services.{SuggestionService, UserService, state}
import views.html._

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class SuggestionController @Inject()(suggestionService: SuggestionService, val messagesApi: MessagesApi)(implicit userService: UserService, executionContext: ExecutionContext, environment: Environment) extends ControllerWithNoAuthRequired {
  def home = AsyncStack { implicit request =>
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(user) => (Some(user.username), user.role == Role.Administrator)
      case _ => (None, false)
    }
    if (isAdmin) {
      suggestionService.listAll.map { suggestions =>
        Ok(index(messagesApi("suggestion"), suggestionHome(SuggestionForm.form, suggestions)))
      }
    } else {
      Future.successful(Ok(index(messagesApi("suggestion"), suggestionHome(SuggestionForm.form, Seq.empty[Suggestion]))))
    }


  }

  def add = AsyncStack() { implicit request =>
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(user) => (Some(user.username), user.role == Role.Administrator)
      case _ => (None, false)
    }
    SuggestionForm.form.bindFromRequest().fold(
      errorForm => Future.successful(Ok(errorForm.toString)),
      data => {
        val text = data.suggestion
        val suggestion = Suggestion(0, text, now, username)
        suggestionService.add(suggestion).map {
          case state.ActionCompleted => Redirect(routes.SuggestionController.home())
          case _ => BadRequest
        }
      }
    )
  }

  private def now = new Timestamp(Calendar.getInstance().getTime.getTime)
}
