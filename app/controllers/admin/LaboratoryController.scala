package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{LaboratoryDAO, UserDAO}
import model.Role._
import model.json.ModelWrites.resultMessageWrites
import model.form.LaboratoryForm
import model.form.data.LaboratoryFormData
import model.{Laboratory, ResultMessage, Role}
import play.Logger
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
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
    LaboratoryForm.form.bindFromRequest.fold(
      errorForm => {
        Logger.error("There was an error with the input" + errorForm)
        Future.successful(Ok)//(index(messagesApi("laboratory.add"),registerLaboratory(errorForm))))
      },
      data => {
        val newLaboratory = Laboratory(0, data.name, data.location, data.administration)
        laboratoryService.add(newLaboratory).map {
          case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Could not add that laboratory"))) //Redirect(normalroutes.HomeController.home())
          case _ => BadRequest(Json.toJson(new ResultMessage("Could not add that laboratory")))

        }
      }
    )
  }

  def addForm() = StackAction(AuthorityKey -> Administrator) { implicit request =>
    play.Logger.debug("Logged user: " + loggedIn)
    implicit val username = Some(loggedIn.username)
    implicit val isAdmin = loggedIn.role == Role.Administrator
    Ok//(index("Add laboratory",registerLaboratory(LaboratoryForm.form)))
  }

  def delete(id: Long) = AuthRequiredAction { implicit request =>
    laboratoryService.delete(id) map {
      case state.ActionCompleted => Ok//Redirect(normalroutes.HomeController.home())
      case state.NotFound => NotFound
      case _ => BadRequest
    }
  }
}
