package controllers

import com.google.inject.Inject
import dao.UserDAO
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model.Role
import model.form.data.LoginFormData
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 14/05/16.
  */
class SuggestionController @Inject()(val messagesApi: MessagesApi,userDAO: UserDAO) extends Controller with I18nSupport with OptionalAuthElement with AuthConfigImpl {
  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def home = AsyncStack { implicit request =>
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(user) => (Some(user.username), user.role == Role.Administrator)
      case _ => (None, false)
    }
    NotImplemented(index(messagesApi("suggestion"),notImplemented(messagesApi("suggestion.notImplemented"))))
  }
}
