package controllers

import com.google.inject.Inject
import dao.{LaboratoryDAO, UserDAO}
import jp.t2v.lab.play2.auth.{AuthElement, AuthenticationElement}
import model.form.LaboratoryForm
import model.form.data.{LaboratoryFormData, LoginFormData}
import model.{Computer, Laboratory, Role, Room}
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import model.Role._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 20/03/16.
  */
class LaboratoryController @Inject()(userDAO: UserDAO,laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport with AuthElement with AuthConfigImpl {

  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def administrateLaboratories = AsyncStack { implicit request =>
    Logger.debug("Petición de listar los laboratorios administrativamente recibida.")
    Future.successful(Redirect(routes.HomeController.home()))
  }

  def edit(id: Long) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    laboratoryDAO.get(id).map { laboratory =>
      laboratory match {
        case Some(laboratory) =>
          val data = LaboratoryFormData(laboratory.name, laboratory.location, laboratory.administration)
          Ok(views.html.index(Some("Admin"), true, "")(views.html.registerLaboratory(LaboratoryForm.form.fill(data))))
        case e => NotFound("Laboratory not found")
      }

    }
  }

  def add = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    Logger.debug("Adding laboratory... ")
    LaboratoryForm.form.bindFromRequest.fold(
      errorForm => {
        Logger.error("There was an error with the input" + errorForm)
        Future.successful(Ok(views.html.index(Some("Admin"), true, "")(views.html.registerLaboratory(errorForm))))
      },
      data => {

        val newLaboratory = Laboratory(0, data.name, data.location, data.administration)
        laboratoryDAO.add(newLaboratory).map(res =>
          Redirect(routes.HomeController.home())
        )
      }
    )
  }

  def addForm = StackAction(AuthorityKey -> Administrator) { implicit request =>
    play.Logger.debug("Logged user: " + loggedIn)
    val username = loggedIn.username
    val isAdmin = loggedIn.role == Role.Administrator
    Ok(views.html.index(Some(username), isAdmin, "Add laboratory")(views.html.registerLaboratory(LaboratoryForm.form)))
  }

  def delete(id: Long) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    laboratoryDAO.delete(id) map { res =>
      Redirect(routes.HomeController.home())
    }
  }

  def get(id: Long) = AsyncStack { implicit request =>
    Logger.debug("Petición de listar el laboratorio " + id + " respondida.")
    laboratoryDAO.getWithChildren(id).map { res =>
      val grouped = res.groupBy(_._1)
      grouped.headOption match {
        case Some((laboratory, rooms)) => {
          val roomsWithComputers = rooms.map {
            row => (row._2,row._3)
          }.groupBy {
            row => row._1
          }.map {
            case (k, v) => (k, v.map(_._2).flatten)
          }.filter {
            row => row._1.isDefined
          }
          Ok(views.html.index(Some("Invitado"), true, "Laboratory" + laboratory.name)(views.html.laboratory(laboratory, roomsWithComputers,true)))
        }
        case e => {
          NotFound("Laboratory not found")
        }
      }

    }
  }

}
