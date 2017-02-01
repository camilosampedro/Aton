package controllers

import com.google.inject.Inject
import model.Role
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import services.{LaboratoryService, UserService}

import scala.concurrent.ExecutionContext

/**
  * Created by camilo on 20/03/16.
  */
class LaboratoryController @Inject()(laboratoryService: LaboratoryService, val messagesApi: MessagesApi)(implicit userService: UserService, environment: Environment, executionContext: ExecutionContext) extends ControllerWithNoAuthRequired {

  def getAll: Action[AnyContent] = Action.async { implicit request=>
    laboratoryService.listAll.map(result => {
      Ok(Json.toJson(result))
    })
  }

  def get(id: Long) = AsyncStack { implicit request =>
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(user) => (Some(user.username), user.role == Role.Administrator)
      case _ => (None, false)
    }
    laboratoryService.get(id).map {
      case Some((laboratoryObject, roomsWithComputers)) => Ok(Json.toJson((laboratoryObject, roomsWithComputers)))
      case _ => NotImplemented//index(messagesApi("laboratory.notFound"),notImplemented(messagesApi("laboratory.notFound"))))
    }
  }

}
