package controllers

import com.google.inject.Inject
import dao.{LaboratoryDAO, UserDAO}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model.form.data.LoginFormData
import model.{Role, User}
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 16/05/16.
  */
class HomeController @Inject()(userDAO: UserDAO, laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport with OptionalAuthElement with AuthConfigImpl {
  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def home = AsyncStack { implicit request =>
    play.Logger.debug("Logged user: " + loggedIn)
    val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(User(username, password, Some(name), role)) => (Some(name), role == Role.Administrator)
      case Some(User(username, password, None, role)) => (Some(username), role == Role.Administrator)
      case _ => (None, false)
    }
    Logger.debug("Petición de listar todos los laboratorios con el siguiente request recibida " + request)
    Logger.debug("User: " + username + ", is admin: " + isAdmin)
    laboratoryDAO.listAll.map { laboratorios =>
      Ok(views.html.index(username, isAdmin, messagesApi("laboratory.laboratoryListTitle"))(views.html.laboratories(laboratorios, isAdmin)))
    }
  }
}
