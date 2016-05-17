package controllers

import com.google.inject.Inject
import dao.{ComputerDAO, RoomDAO}
import model.form.data.ComputerFormData
import model.{Computer, Room}
import model.form.{ComputerForm, ComputerFormPre}
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller, Result}
import play.mvc.Results
import services.{ComputerService, SSHOrderService}
import services.exec.{Execution, SSHFunction}
import views.html.laboratory

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

/**
  * Created by camilo on 7/05/16.
  */
class ComputerController @Inject()(sSHOrderService: SSHOrderService, computerService: ComputerService, roomDAO: RoomDAO, computerDAO: ComputerDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index = Action.async { implicit request =>
    computerDAO.listAll map { computers =>
      Ok("Computers")
    }
  }

  def edit = Action.async { implicit request =>
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
                      Ok(views.html.index(Some("Admin"), true, "")(views.html.editComputer(errorForm, pairs)))
                    case _ =>
                      NotFound("Computer has not roomPanel")
                  }
                }
                case _ => val rooms = Await.result(roomDAO.listAll, 5 seconds)
                  val pairs = rooms.map(x => (x.id.toString, x.laboratoryID + x.name))

                  val computerForm = ComputerFormData(computer.ip, computer.name, computer.SSHUser, computer.SSHPassword, computer.description, None)
                  Ok(views.html.index(Some("Admin"), true, "")(views.html.editComputer(ComputerForm.form.fill(computerForm), pairs)))

              }
              Ok
            case _ => NotFound("Computer not found")
          }
        }
      },
      data => {
        val newComputer = Computer(data.ip, data.name, None, data.SSHUser, data.SSHPassword, data.description, data.roomID)
        computerService.add(newComputer).map { res =>
          Redirect(routes.HomeController.home())
        }
      }
    )
  }

  def add(laboratoryId: Long) = Action.async { implicit request =>
    Logger.debug("Request de agregar equipo ingresada:" + request)
    ComputerFormPre.form.bindFromRequest.fold(
      errorForm => Future.successful(Ok(errorForm.toString)),
      data => {
        val newComputer = Computer(data.ip, None, None, data.SSHUser, data.SSHPassword, None, None)
        Logger.debug("Adding a new computer: " + newComputer)
        sSHOrderService.getMac(newComputer) match {
          case Some(mac) => {
            val newComputerWithMac = newComputer.copy(mac = Some(mac))
            Logger.debug("Adding to database the following computer: " + newComputerWithMac)
            computerDAO.add(newComputerWithMac).map { res =>
              Redirect(routes.ComputerController.editForm(newComputer.ip))
            }
          }
          case _ => {
            Future.successful(InternalServerError)
          }
        }
      }
    )
  }

  def editForm(ip: String) = Action.async {
    implicit request =>
      Logger.debug("Looking for computer: " + ip)
      val resultados = for {
        computerSearch <- computerDAO.get(ip)
        roomsSearch <- roomDAO.listAll
      } yield (computerSearch, roomsSearch)

      resultados.map(res =>
        res._1 match {
          case Some(Computer(ip, name, _, sSHUser, sSHPassword, description, room)) => {
            val computerForm = ComputerFormData(ip, name, sSHUser, sSHPassword, description, room)
            val pairs = res._2.map(x => (x.id.toString, x.name))
            Ok(views.html.index(Some("Admin"), true, "")(views.html.editComputer(ComputerForm.form.fill(computerForm), pairs)))
          }
          case _ =>
            Logger.debug("The computer was not found")
            NotFound("Computer not found")
        }
      )

    /*computerDAO.get(ip).map { res =>
      Logger.debug("Computer gotten: " + res.toString)
      res match {
        case Some(computer) => {
          Logger.debug("The computer was found")
          computer.roomID match {
            case Some(roomID) => {
              Logger.debug("The computer has a roomPanel: " + roomID)
              roomDAO.get(roomID).onComplete { result =>
                result match {
                  case Success(Some(roomPanel)) => {
                    roomDAO.getByLaboratory(roomPanel.id).onComplete { result2 =>
                      result2 match {
                        case Success(rooms) => {
                          val pairs = rooms.map(x => (x.id.toString, x.name))
                          val computerForm = ComputerFormData(computer.ip, computer.name, computer.SSHUser, computer.SSHPassword, computer.description, roomID)
                          Ok(views.html.index(Some("Admin"), true, "")(views.html.editComputer(ComputerForm.form.fill(computerForm), pairs, roomPanel.laboratoryID)))
                        }
                        case Failure(e) => {
                          Logger.error("Error al obtener la sala", e)
                          NotFound(e.getMessage)
                        }
                        case _ =>
                          NotFound
                      }

                    }
                  }
                  case Failure(e) =>
                    Logger.error("Error al obtener", e)
                    NotFound(e.getMessage)
                  case _ =>
                    NotFound("Computer has not roomPanel")
                }
              }
            }
            case _ => {
              Logger.debug("The computer has not a roomPanel")
              roomDAO.listAll.onComplete { result =>
                val x: Result = result match {
                  case Success(rooms) =>
                    Logger.debug("Looking for all rooms then: " + rooms)
                    val pairs = rooms.map(x => (x.id.toString, x.laboratoryID + x.name))
                    val computerForm = ComputerFormData(computer.ip, computer.name, computer.SSHUser, computer.SSHPassword, computer.description, 0)
                    Ok(views.html.index(Some("Admin"), true, "")(views.html.editComputer(ComputerForm.form.fill(computerForm), pairs, 0)))
                  case Failure(e) =>
                    Logger.error("Error looking for all rooms", e)
                    NotFound(e.getMessage)
                  case _ =>
                    NotFound
                }
                return x
              }

            }
          }
        }
        case _ => {
          Logger.debug("The computer was found")
          ComputerController.this.NotFound("Computer not found")
        }
      }
    }
    */
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
          Redirect(routes.HomeController.home())
      }
  }


}
