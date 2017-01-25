package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import model.Room
import model.json.ResultMessage
import play.Logger
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsError, JsSuccess, Json}
import services.{LaboratoryService, RoomService, UserService, state}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class RoomController @Inject()(roomService: RoomService, laboratoryService: LaboratoryService, val messagesApi: MessagesApi)(implicit userService: UserService, executionContext: ExecutionContext, environment: Environment) extends ControllerWithAuthRequired {

  def add = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    Logger.debug("Adding room... ")
    request.body.asJson match {
      case Some(json) => json.validate[Room] match {
        case JsSuccess(room, _) => roomService.add(room).map {
          case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Room added")))
          case _ => BadRequest(Json.toJson(new ResultMessage("Could not add that room")))
        }
        case JsError(errors) => Future.successful(BadRequest(Json.toJson(ResultMessage.wrongJsonFormat(errors))))
      }
      case _ => Future.successful(BadRequest(Json.toJson(ResultMessage.inputWasNotAJson)))
    }
  }

  def update = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    Logger.debug("Updating room... ")
    request.body.asJson match {
      case Some(json) => json.validate[Room] match {
        case JsSuccess(room, _) => roomService.update(room).map {
          case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Room updated")))
          case state.NotFound => NotFound(Json.toJson( ResultMessage("Room not found", Seq(("id", room.id.toString)))))
          case _ => BadRequest(Json.toJson(new ResultMessage("Could not update that room")))
        }
        case JsError(errors) => Future.successful(BadRequest(Json.toJson(ResultMessage.wrongJsonFormat(errors))))
      }
      case _ => Future.successful(BadRequest(Json.toJson(ResultMessage.inputWasNotAJson)))
    }
  }

  def delete(roomId: Long) = AuthRequiredAction { implicit request =>
    roomService.delete(roomId).map {
      case state.ActionCompleted => Redirect(normalroutes.HomeController.home())
      case state.NotFound => NotFound
      case _ => BadRequest
    }
  }

  def blockUser(roomId: Long) = AuthRequiredAction { implicit request =>
    // TODO: Processing Not Yet Implemented
    val results = for {
      roomResult <- roomService.get(roomId)
    } yield roomResult
    results.map { result: Option[Room] =>
      if (result.isDefined)
        Redirect(normalroutes.LaboratoryController.get(result.get.laboratoryID))
      else
        NotFound
    }
  }
}
