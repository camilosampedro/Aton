package controllers

import com.google.inject.Inject
import dao.UserDAO
import model.Role
import model.form.data.LoginFormData
import play.api.Environment
import play.api.i18n.MessagesApi
import services.LaboratoryService
import views.html._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 20/03/16.
  */
class LaboratoryController @Inject()(laboratoryService: LaboratoryService, val messagesApi: MessagesApi)(implicit  userDAO: UserDAO, environment: Environment, executionContext: ExecutionContext) extends ControllerWithNoAuthRequired {
  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def get(id: Long) = AsyncStack { implicit request =>
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(user) => (Some(user.username), user.role == Role.Administrator)
      case _ => (None, false)
    }
    laboratoryService.get(id).map {
      case Some((laboratoryObject, roomsWithComputers)) => Ok(index("Laboratory" + laboratoryObject.name,laboratory(laboratoryObject, roomsWithComputers)))
      case _ => NotImplemented(index(messagesApi("laboratory.notFound"),notImplemented(messagesApi("laboratory.notFound"))))
    }
  }

}
