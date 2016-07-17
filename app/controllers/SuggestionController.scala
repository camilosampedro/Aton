package controllers

import java.sql.Timestamp
import java.util.Calendar

import com.google.inject.Inject
import dao.{SuggestionDAO, UserDAO}
import model.form.SuggestionForm
import model.{Role, Suggestion}
import play.api.i18n.MessagesApi
import views.html._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 14/05/16.
  */
class SuggestionController @Inject()(suggestionDAO: SuggestionDAO, val messagesApi: MessagesApi)(implicit userDAO: UserDAO, executionContext: ExecutionContext) extends ControllerWithNoAuthRequired {
  def home = AsyncStack { implicit request =>
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(user) => (Some(user.username), user.role == Role.Administrator)
      case _ => (None, false)
    }
    if (isAdmin) {
      suggestionDAO.listAll.map { res =>
        Ok(index(messagesApi("suggestion"), suggestionHome(SuggestionForm.form, res)))
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
        suggestionDAO.add(suggestion).map { res =>
          Redirect(routes.SuggestionController.home())
        }
      }
    )
  }

  private def now = new Timestamp(Calendar.getInstance().getTime.getTime)
}
