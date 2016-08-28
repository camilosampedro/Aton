package controllers

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import dao.{LaboratoryDAO, UserDAO}
import model.{Role, User}
import play.api.Logger
import play.api.i18n.MessagesApi
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by camilo on 16/05/16.
  */
@Singleton
class HomeController @Inject()(laboratoryDAO: LaboratoryDAO, @Named("computerChecker") computerChecker: ActorRef, actorSystem: ActorSystem, val messagesApi: MessagesApi)(implicit userDAO: UserDAO, executionContext: ExecutionContext) extends ControllerWithNoAuthRequired {
  val logger = Logger("HomeController")

  play.Logger.debug("Computer Checker configured.")
  actorSystem.scheduler.schedule(0.microseconds,5.minutes, computerChecker,"Execute")

  def home = AsyncStack { implicit request =>
    //play.Logger..debug("Logged user: " + loggedIn
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(User(usernameString, password, Some(name), role)) => (Some(name), role == Role.Administrator)
      case Some(User(usernameString, password, None, role)) => (Some(usernameString), role == Role.Administrator)
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
      case Some(User(usernameString, password, Some(name), role)) => (Some(name), role == Role.Administrator)
      case Some(User(usernameString, password, None, role)) => (Some(usernameString), role == Role.Administrator)
      case _ => (None, false)
    }

    Ok(index(messagesApi("about"),views.html.about()))
  }
}
