package controllers

import com.google.inject.Inject
import dao.LaboratoryDAO
import model.form.LaboratoryForm
import model.form.data.LaboratoryFormData
import model.{Computer, Laboratory, Room}
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by camilo on 20/03/16.
  */
class LaboratoryController @Inject()(laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def listAll = Action.async { implicit request =>
    Logger.debug("Petición de listar todos los laboratorios con el siguiente request recibida " + request)
    laboratoryDAO.listAll.map { laboratorios =>
      Ok(views.html.index(Some("Invitado"), true, "Lista de laboratorios")(views.html.laboratories(laboratorios, true)))
    }
  }


  def administrateLaboratories = Action.async { implicit request =>
    Logger.debug("Petición de listar los laboratorios administrativamente recibida.")
    Future.successful(Redirect(routes.LaboratoryController.listAll()))
  }

  def edit(id: Long) = Action.async { implicit request =>
    laboratoryDAO.get(id).map { laboratory =>
      laboratory match {
        case Some(laboratory) =>
          val data = LaboratoryFormData(laboratory.name, laboratory.location, laboratory.administration)
          Ok(views.html.index(Some("Admin"), true, "")(views.html.registerLaboratory(LaboratoryForm.form.fill(data))))
        case e => NotFound("Laboratory not found")
      }

    }
  }

  def add = Action.async { implicit request =>
    Logger.debug("Adding laboratory... ")
    LaboratoryForm.form.bindFromRequest.fold(
      errorForm => {
        Logger.error("There was an error with the input" + errorForm)
        Future.successful(Ok(views.html.index(Some("Admin"), true, "")(views.html.registerLaboratory(errorForm))))
      },
      data => {

        val newLaboratory = Laboratory(0, data.name, data.location, data.administration)
        laboratoryDAO.add(newLaboratory).map(res =>
          Redirect(routes.LaboratoryController.listAll())
        )
      }
    )
  }

  def addForm = Action { implicit request =>
    Ok(views.html.index(Some("Admin"), true, "")(views.html.registerLaboratory(LaboratoryForm.form)))
  }

  def delete(id: Long) = Action.async { implicit request =>
    laboratoryDAO.delete(id) map { res =>
      Redirect(routes.LaboratoryController.listAll())
    }
  }

  def get(id: Long) = Action.async { implicit request =>
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
