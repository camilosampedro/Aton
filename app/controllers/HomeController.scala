package controllers

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import dao.{DatabaseInitializer, LaboratoryDAO, UserDAO}
import model.{Role, User}
import play.api.{Environment, Logger}
import play.api.i18n.MessagesApi
import services.{LaboratoryService, UserService}
import views.html._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@Singleton
class HomeController @Inject()(databaseInitializer: DatabaseInitializer, laboratoryService : LaboratoryService, @Named("computerChecker") computerChecker: ActorRef, actorSystem: ActorSystem, val messagesApi: MessagesApi)(implicit userService: UserService, executionContext: ExecutionContext, environment: Environment) extends ControllerWithNoAuthRequired {
  val logger = Logger("HomeController")

  play.Logger.debug("Configuring Computer Checker...")
  actorSystem.scheduler.schedule(0.microseconds,5.minutes, computerChecker,"Execute")
  play.Logger.debug("Computer Checker configured.")

  logger.debug("Initializing database")
  Await.result(databaseInitializer.initialize(), 2.seconds)
  logger.debug("Database initialized")

  def home = AsyncStack { implicit request =>
    play.Logger.debug("Logged user: " + loggedIn)
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(User(usernameString, password, Some(name), role)) => (Some(name), role == Role.Administrator)
      case Some(User(usernameString, password, None, role)) => (Some(usernameString), role == Role.Administrator)
      case _ => (None, false)
    }
    logger.debug("Petición de listar todos los laboratorios con el siguiente request recibida " + request)
    logger.debug("User: " + username + ", is admin: " + isAdmin)
    laboratoryService.listAll.map { labs =>
      Ok(index(messagesApi("laboratory.list.title")))
    }
  }

  def about = StackAction { implicit request =>
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(User(usernameString, password, Some(name), role)) => (Some(name), role == Role.Administrator)
      case Some(User(usernameString, password, None, role)) => (Some(usernameString), role == Role.Administrator)
      case _ => (None, false)
    }

    Ok//(index(messagesApi("about"),views.html.about()))
  }
}
