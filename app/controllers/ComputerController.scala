package controllers

import com.google.inject.Inject
import dao.{ComputerDAO, RoomDAO}
import model.form.data.ComputerFormData
import model.{Computer, Room}
import model.form.{ComputerForm, ComputerFormPre}
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import services.exec.{Execution, SSHFunction}
import views.html.laboratory

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

/**
  * Created by camilo on 7/05/16.
  */
class ComputerController @Inject()(roomDAO: RoomDAO, computerDAO: ComputerDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index = Action.async { implicit request =>
    computerDAO.listAll map { computers =>
      Ok("Computers")
    }
  }

  def edit(laboratoryId: Long) = Action.async { implicit request =>
    ComputerForm.form.bindFromRequest().fold(
      errorForm => {
        computerDAO.get(errorForm.get.ip).map { res =>
          res match {
            case Some(computer) =>
              computer.roomID match {
                case Some(roomID) => {
                  Await.result(roomDAO.get(roomID), 5 seconds) match {
                    case Some(room) =>
                      val rooms = Await.result(roomDAO.getByLaboratory(room.id), 5 seconds)
                      val pairs = rooms.map(x => (x.id.toString, x.name))
                      Ok(views.html.index(Some("Admin"), true, "")(views.html.editComputer(errorForm, pairs, room.laboratoryID)))
                    case _ =>
                      NotFound("Computer has not room")
                  }
                }
                case _ => val rooms = Await.result(roomDAO.listAll, 5 seconds)
                  val pairs = rooms.map(x => (x.id.toString, x.laboratoryID + x.name))
                  val computerForm = ComputerFormData(computer.ip, computer.name, computer.SSHUser, computer.SSHPassword, computer.description, 0)
                  Ok(views.html.index(Some("Admin"), true, "")(views.html.editComputer(ComputerForm.form.fill(computerForm), pairs, 0)))

              }
              Ok
            case _ => NotFound("Computer not found")
          }
        }
      },
      data => {
        val newComputer = Computer(data.ip, data.name, "", data.SSHUser, data.SSHPassword, data.description, Some(data.roomID))
        computerDAO.add(newComputer).map { res =>
          Redirect(routes.LaboratoryController.get(laboratoryId))
        }
      }
    )
  }

  def add(laboratoryId: Long) = Action.async { implicit request =>
    Logger.debug("Request de agregar equipo ingresada:" + request)
    ComputerFormPre.form.bindFromRequest.fold(
      errorForm => Future.successful(Ok(errorForm.toString)),
      data => {
        val newComputer = Computer(data.ip, "", "", data.SSHUser, data.SSHPassword, "", None)
        Logger.debug("Adding a new computer: " + newComputer)
        val (result, exitCode) = Execution.execute(newComputer, SSHFunction.ES_MAC_ORDER2)
        if (exitCode == 0) {
          computerDAO.add(newComputer).map { res =>
            Redirect(routes.ComputerController.editForm(newComputer.ip))
          }
        } else {
          Future.successful(InternalServerError)
        }

      }
    )
  }

  def editForm(ip: String) = Action.async { implicit request =>
    Logger.debug("Obteniendo equipo " + ip)
    computerDAO.get(ip).map { res =>
      Logger.debug(res.toString)
      res match {
        case Some(computer) =>
          computer.roomID match {
            case Some(roomID) =>
              roomDAO.get(roomID).onComplete { result =>
                result match {
                  case Success(Some(room)) => {
                    Logger.debug("Pasó la primera")

                    roomDAO.getByLaboratory(room.id).onComplete { result2 =>
                      result2 match {
                        case Success(rooms) => {
                          Logger.debug("Pasó la segunda")
                          val pairs = rooms.map(x => (x.id.toString, x.name))
                          val computerForm = ComputerFormData(computer.ip, computer.name, computer.SSHUser, computer.SSHPassword, computer.description, roomID)
                          Ok(views.html.index(Some("Admin"), true, "")(views.html.editComputer(ComputerForm.form.fill(computerForm), pairs, room.laboratoryID)))
                        }
                        case Failure(e) => {
                          Logger.error("Error al obtener la sala", e)
                        }
                      }

                    }
                  }
                  case Failure(e) =>
                    Logger.error("Error al obtener" , e)
                  case _ =>
                    NotFound("Computer has not room")
                }


              }
            case _ =>
              val rooms = Await.result(roomDAO.listAll, 500 millis)
              val pairs = rooms.map(x => (x.id.toString, x.laboratoryID + x.name))
              val computerForm = ComputerFormData(computer.ip, computer.name, computer.SSHUser, computer.SSHPassword, computer.description, 0)
              Ok(views.html.index(Some("Admin"), true, "")(views.html.editComputer(ComputerForm.form.fill(computerForm), pairs, 0)))
          }

          Ok
        case _ => NotFound("Computer not found")
      }
    }
  }

  def addForm(laboratoryId: Long) = Action.async {
    implicit request =>
      roomDAO.getByLaboratory(laboratoryId).map {
        rooms =>
          val pairs = rooms.map(x => (x.id.toString, x.name))
          Ok(views.html.index(Some("Admin"), true, "")(views.html.registerComputer(ComputerFormPre.form, laboratoryId)))
      }
  }

  def delete(ip: String) = Action.async {
    implicit request =>
      computerDAO.delete(ip) map {
        res =>
          Redirect(routes.ComputerController.index())
      }
  }

}
