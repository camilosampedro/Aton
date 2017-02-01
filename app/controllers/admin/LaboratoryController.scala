package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import model.json.{LaboratoryJson, ResultMessage}
import model.{Laboratory, Role}
import play.Logger
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsError, JsSuccess, Json}
import services.{LaboratoryService, UserService, state}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class LaboratoryController @Inject()(laboratoryService: LaboratoryService, val messagesApi: MessagesApi)(implicit userService: UserService, executionContext: ExecutionContext, environment: Environment) extends ControllerWithAuthRequired {

  def update = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    implicit val isAdmin = loggedIn.role == Role.Administrator
    // TODO: Get id from json
    request.body.asJson match {
      case Some(json) => json.validate[Laboratory] match {
        case JsSuccess(laboratory, _) =>
          laboratoryService.update(laboratory).map {
            case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Laboratory updated")))
            case state.NotFound => NotFound(Json.toJson(ResultMessage("Laboratory not found",Seq(("id", laboratory.id.toString)))))
            case _ => BadRequest(Json.toJson(new ResultMessage("Could not add that laboratory")))
          }
        case JsError(errors) =>Future.successful(BadRequest(Json.toJson(ResultMessage.wrongJsonFormat(errors))))
      }
      case _ => Future.successful(BadRequest(Json.toJson(ResultMessage.inputWasNotAJson)))
    }
  }

  def add = AuthRequiredAction { implicit request =>
    Logger.debug("Adding laboratory... ")
    implicit val username = Some(loggedIn.username)
    implicit val isAdmin = loggedIn.role == Role.Administrator
    request.body.asJson match {
      case Some(json) => json.validate[LaboratoryJson] match {
        case JsSuccess(laboratory, _) =>
          val newLaboratory = Laboratory(0, laboratory.name, laboratory.location, laboratory.administration)
          laboratoryService.add(newLaboratory).map {
            case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Laboratory added")))
            case _ => BadRequest(Json.toJson(new ResultMessage("Could not add that laboratory")))

          }
        case JsError(errors) =>Future.successful(BadRequest(Json.toJson(ResultMessage.wrongJsonFormat(errors))))
      }
      case _ => Future.successful(BadRequest(Json.toJson(ResultMessage.inputWasNotAJson)))
    }
  }

  def delete(id: Long) = AuthRequiredAction { implicit request =>
    laboratoryService.delete(id) map {
      case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Laboratory deleted successfully")))
      case state.NotFound => NotFound(Json.toJson(new ResultMessage("Laboratory not found")))
      case _ => BadRequest(Json.toJson(new ResultMessage("A server problem occurred while trying to delete the laboratory")))
    }
  }
}
