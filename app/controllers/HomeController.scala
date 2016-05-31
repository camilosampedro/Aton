package controllers

import akka.actor.{ActorRef, ActorSystem, Props}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import dao.{LaboratoryDAO, UserDAO}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model.form.data.LoginFormData
import model.{Role, User}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import views.html._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

/**
  * Created by camilo on 16/05/16.
  */
@Singleton
class HomeController @Inject()(userDAO: UserDAO, laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi, @Named("computerChecker") computerChecker: ActorRef, actorSystem: ActorSystem) extends Controller with I18nSupport with OptionalAuthElement with AuthConfigImpl {
  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  val logger = Logger("HomeController")

  play.Logger.debug("Computer Checker configured.")
  actorSystem.scheduler.schedule(0.microseconds,10.minutes, computerChecker,"Execute")

  def home = AsyncStack { implicit request =>
    //play.Logger..debug("Logged user: " + loggedIn
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(User(username, password, Some(name), role)) => (Some(name), role == Role.Administrator)
      case Some(User(username, password, None, role)) => (Some(username), role == Role.Administrator)
      case _ => (None, false)
    }
    //logger.debug("Petición de listar todos los laboratorios con el siguiente request recibida " + request)
    //logger.debug("User: " + username + ", is admin: " + isAdmin)
    laboratoryDAO.listAll.map { laboratorios =>
      Ok(index(messagesApi("laboratory.laboratoryListTitle"),laboratories(laboratorios)))
    }
  }

  def about = StackAction { implicit request =>
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(User(username, password, Some(name), role)) => (Some(name), role == Role.Administrator)
      case Some(User(username, password, None, role)) => (Some(username), role == Role.Administrator)
      case _ => (None, false)
    }

    Ok(index(messagesApi("about"),views.html.about()))
  }
}
