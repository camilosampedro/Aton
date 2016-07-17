package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{LaboratoryDAO, UserDAO}
import model.Role._
import model.form.LaboratoryForm
import model.form.data.LaboratoryFormData
import model.{Laboratory, Role}
import play.Logger
import play.api.i18n.MessagesApi
import views.html._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 20/03/16.
  */
class LaboratoryController @Inject()(laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi)(implicit userDAO: UserDAO, executionContext: ExecutionContext) extends ControllerWithAuthRequired {
  def administrateLaboratories = AuthRequiredAction { implicit request =>
    Logger.debug("Petición de listar los laboratorios administrativamente recibida.")
    Future.successful(Redirect(normalroutes.HomeController.home()))
  }

  def edit(id: Long) = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    implicit val isAdmin = loggedIn.role == Role.Administrator
    laboratoryDAO.get(id).map {
      case Some(laboratory) =>
        val data = LaboratoryFormData(laboratory.name, laboratory.location, laboratory.administration)
        Ok(index(messagesApi("laboratory.edit"), registerLaboratory(LaboratoryForm.form.fill(data))))
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
        Future.successful(Ok(index(messagesApi("laboratory.add"),registerLaboratory(errorForm))))
      },
      data => {

        val newLaboratory = Laboratory(0, data.name, data.location, data.administration)
        laboratoryDAO.add(newLaboratory).map(res =>
          Redirect(normalroutes.HomeController.home())
        )
      }
    )
  }

  def addForm() = StackAction(AuthorityKey -> Administrator) { implicit request =>
    play.Logger.debug("Logged user: " + loggedIn)
    implicit val username = Some(loggedIn.username)
    implicit val isAdmin = loggedIn.role == Role.Administrator
    Ok(index("Add laboratory",registerLaboratory(LaboratoryForm.form)))
  }

  def delete(id: Long) = AuthRequiredAction { implicit request =>
    laboratoryDAO.delete(id) map { res =>
      Redirect(normalroutes.HomeController.home())
    }
  }
}
