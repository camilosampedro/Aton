package controllers

import com.google.inject.Inject
import dao.{LaboratoryDAO, RoomDAO}
import model.Room
import model.form.RoomForm
import model.form.data.RoomFormData
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import play.Logger

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by camilosampedro on 11/05/16.
  */
class RoomController @Inject()(roomDAO: RoomDAO, laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def add = Action.async { implicit request =>
    Logger.debug("Addig roomPanel... ")
    RoomForm.form.bindFromRequest().fold(
      errorForm => {
        Logger.error("There was an error with the input" + errorForm)
        laboratoryDAO.listAll.map { laboratories =>
          val pairs = laboratories.map(x => (x.id.toString, x.name))
          Ok(views.html.index(Some("Admin"), true, "")(views.html.registerRoom(errorForm, pairs)))
        }
      },
      data => {
        val newRoom = Room(0, data.name, data.audiovisualResources, data.basicTools, data.laboratoryID)
        roomDAO.add(newRoom).map { res =>
          Redirect(routes.RoomController.addForm)
        }
      }
    )
  }

  def addForm = Action.async { implicit request =>
    laboratoryDAO.listAll.map { laboratories =>
      val pairs = laboratories.map(x => (x.id.toString, x.name))
      Ok(views.html.index(Some("Admin"), true, "")(views.html.registerRoom(RoomForm.form, pairs)))
    }
  }

  def editForm(roomId: Long) = Action.async { implicit request =>
    val results = for {
      roomResult <- roomDAO.get(roomId)
      laboratoriesResult <- laboratoryDAO.listAll
    } yield (roomResult, laboratoriesResult)
    results.map{res =>
      res._1 match {
        case Some(room) => {
          val roomFormData = RoomFormData(room.name, room.audiovisualResources, room.basicTools, room.laboratoryID)
          val pairs = res._2.map(x => (x.id.toString, x.name))
          Ok(views.html.index(Some("Admin"), true, "")(views.html.registerRoom(RoomForm.form.fill(roomFormData), pairs)))
        }
        case _ => {
          NotImplemented(messagesApi("room.notFound"))
        }
      }
    }
  }

  def edit(roomId:Long) = Action {implicit request =>
    NotImplemented
  }

  def delete(roomId: Long) = Action.async {implicit request =>
    roomDAO.delete(roomId).map{ res =>
      Redirect(routes.HomeController.home())
    }
  }
}
