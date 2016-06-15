package controllers

import java.sql.Timestamp
import java.util.Calendar

import com.google.inject.Inject
import dao.{SuggestionDAO, UserDAO}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model.{Computer, Role, Suggestion}
import model.form.SuggestionForm
import model.form.data.LoginFormData
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 14/05/16.
  */
class SuggestionController @Inject()(suggestionDAO: SuggestionDAO, val messagesApi: MessagesApi, userDAO: UserDAO) extends Controller with I18nSupport with OptionalAuthElement with AuthConfigImpl {
  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def home = AsyncStack { implicit request =>
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(user) => (Some(user.username), user.role == Role.Administrator)
      case _ => (None, false)
    }
    if(isAdmin) {
      suggestionDAO.listAll.map{res=>
        Ok(index(messagesApi("suggestion"),suggestionHome(SuggestionForm.form,res)))
      }
    } else {
      Future.successful(Ok(index(messagesApi("suggestion"),suggestionHome(SuggestionForm.form,Seq.empty[Suggestion]))))
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
        val suggestion = Suggestion(0,text,now,username)
        suggestionDAO.add(suggestion).map{res=>
          Ok(index(messagesApi("suggestion"),suggestionHome(SuggestionForm.form)))
        }
      }
    )
  }

  private def now = new Timestamp(Calendar.getInstance().getTime.getTime)
}
