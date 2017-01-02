package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{LaboratoryDAO, UserDAO}
import model.Role._
import model.form.LaboratoryForm
import model.form.data.LaboratoryFormData
import model.json.{LaboratoryJson, ResultMessage}
import model.{Laboratory, Role}
import play.Logger
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, AnyContent}
import services.{LaboratoryService, UserService, state}
import views.html._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class LaboratoryController @Inject()(laboratoryService: LaboratoryService, val messagesApi: MessagesApi)(implicit userService: UserService, executionContext: ExecutionContext, environment: Environment) extends ControllerWithAuthRequired {

  def edit = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    implicit val isAdmin = loggedIn.role == Role.Administrator
    // TODO: Get id from json
    val id = 1
    laboratoryService.getSingle(id).map {
      case Some(laboratory) =>
        val data = LaboratoryFormData(laboratory.name, laboratory.location, laboratory.administration)
        Ok//(index(messagesApi("laboratory.edit"), registerLaboratory(LaboratoryForm.form.fill(data))))
      case e => NotFound("Laboratory not found")
    }
  }

  def add = AuthRequiredAction { implicit request =>
    Logger.debug("Adding laboratory... ")
    implicit val username = Some(loggedIn.username)
    implicit val isAdmin = loggedIn.role == Role.Administrator
    request.body.asJson match {
      case Some(json) => json.validate[LaboratoryJson] match {
        case JsSuccess(laboratory, path) =>
          val newLaboratory = Laboratory(0, laboratory.name, laboratory.location, laboratory.administration)
          laboratoryService.add(newLaboratory).map {
            case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Could not add that laboratory"))) //Redirect(normalroutes.HomeController.home())
            case _ => BadRequest(Json.toJson(new ResultMessage("Could not add that laboratory")))

          }
        case JsError(errors) =>Future.successful(BadRequest(Json.toJson(ResultMessage("Json format error", errors.map(_.toString)))))
      }
      case _ => Future.successful(BadRequest(Json.toJson(new ResultMessage("Non json not expected"))))
    }
  }

  def delete(id: Long) = AuthRequiredAction { implicit request =>
    laboratoryService.delete(id) map {
      case state.ActionCompleted => Ok//Redirect(normalroutes.HomeController.home())
      case state.NotFound => NotFound
      case _ => BadRequest
    }
  }
}
