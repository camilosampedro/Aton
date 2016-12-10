package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import model.{Computer, ResultMessage}
import model.json.ModelWrites.resultMessageWrites
import model.form._
import model.form.data.ComputerFormData
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import services._
import views.html._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class ComputerController @Inject()(computerService: ComputerService, roomService: RoomService, val messagesApi: MessagesApi)(implicit userService: UserService, executionContext: ExecutionContext, environment: Environment) extends ControllerWithAuthRequired {

  def add = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    play.Logger.debug("Add computer request received:" + request)
    ComputerForm.form.bindFromRequest.fold(
      errorForm => Future.successful(Ok),//(errorForm.toString)),
      data => {
        computerService.add(data.ip, data.name, data.SSHUser, data.SSHPassword, data.description, data.roomID).map {
          case state.ActionCompleted => Ok//(Json.toJson(new ResultMessage("Computer added successfully")))
          case state.NotFound => NotFound
          case _ => BadRequest(Json.toJson(new ResultMessage("Could not add that computer")))
        }
      })
  }

  def addForm = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      roomService.listAll.map {
        rooms =>
          val pairs = rooms.map(x => (x.id.toString, x.name))
          Ok//(index(messagesApi("computer.add"), registerComputer(ComputerForm.form, pairs)))
      }
  }

  def blockPage(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      BlockPageForm.form.bindFromRequest.fold(
        errorForm => {
          play.Logger.error(errorForm.toString)
          play.Logger.error(errorForm.errors.toString)
          Future.successful(BadRequest)
        },
        data => {
          computerService.blockPage(ip, data.page).map {
            case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(ResultMessage("Page blocked successfully on the computer", Seq(exitCode.toString, result))))
            case state.NotFound => NotFound
            case state.OrderFailed(result, exitCode) => BadRequest(Json.toJson(ResultMessage("Could not block that page", Seq(result))))
            case _ => BadRequest(Json.toJson(new ResultMessage("Could not block that page")))
          }
        })
  }

  def delete(ip: String) = AuthRequiredAction {
    implicit request =>
      computerService.delete(ip) map {
        case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computer deleted successfully")))
        case state.NotFound => NotFound
        case _ => BadRequest(Json.toJson(new ResultMessage("Could not delete that computer")))
      }
  }

  def edit = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    ComputerForm.form.bindFromRequest().fold(
      errorForm => {
        play.Logger.debug(errorForm.errors.mkString)
        play.Logger.debug(errorForm.toString)

        computerService.getSingle(errorForm.get.ip).map {
          case Some(Computer(ip, _, _, _, _, Some(roomID))) =>
            val foundRoom = Await.result(roomService.get(roomID), 5 seconds)
            foundRoom match {
              case Some(room) =>
                val rooms = Await.result(roomService.getByLaboratory(room.laboratoryID), 5 seconds)
                val pairs = rooms.map(singleRoom => (singleRoom.id.toString, singleRoom.name))
                Ok//(index(messagesApi("computer.edit"), editComputer(errorForm, pairs)))
              case _ => NotFound(Json.toJson(ResultMessage("Could not edit that computer, computer has not an associated" +
                " room and there are form errors", errorForm.errors.map(_.toString))))
            }
          case _ => BadRequest(Json.toJson(ResultMessage("Could not edit that computer, form errors",
            errorForm.errors.map(_.toString))))
        }
      },
      data => {
        val newComputer = Computer(data.ip, data.name, data.SSHUser, data.SSHPassword, data.description, data.roomID)
        computerService.edit(newComputer).map {
          case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computer edited successfully")))
          case state.NotFound => NotFound
          case _ => BadRequest(Json.toJson(new ResultMessage("Could not edit that computer")))
        }
      })
  }

  def editForm(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      play.Logger.debug("Looking for computer: " + ip)
      val results = for {
        computerSearch <- computerService.getSingle(ip)
        roomsSearch <- roomService.listAll
      } yield (computerSearch, roomsSearch)
      results.map(res =>
        res._1 match {
          case Some(Computer(`ip`, name, sSHUser, sSHPassword, description, room)) =>
            val computerForm = ComputerFormData(ip, name, sSHUser, sSHPassword, description, room)
            val pairs = res._2.map(x => (x.id.toString, x.name))
            Ok//(index(messagesApi("computer.edit"), editComputer(ComputerForm.form.fill(computerForm), pairs)))
          case _ =>
            play.Logger.debug("The computer was not found")
            NotFound("Computer not found")
        })
  }


  def shutdown(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerService.shutdown(ip).map {
        case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computer shutdown successfully")))
        case state.NotFound => NotFound
        case _ => BadRequest(Json.toJson(new ResultMessage("Could not shutdown that computer")))
      }
  }

  def shutdownSeveral() = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      SelectComputersForm.form.bindFromRequest().fold(
        errorForm => Future.successful(BadRequest),
        data => {
          computerService.shutdown(data.selectedComputers).map {
            case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computers shutdown successfully")))
            case state.NotFound => NotFound
            case _ => BadRequest(Json.toJson(new ResultMessage("Could not shutdown those computers")))
          }
        })
  }

  def upgrade(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerService.upgrade(ip).map {
        case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(ResultMessage("Computer upgraded successfully", Seq(result))))
        case state.NotFound => NotFound
        case state.OrderFailed(result, exitCode) =>
          BadRequest(Json.toJson(ResultMessage("Could not upgrade that computer", Seq(result))))
        case _ => BadRequest
      }
  }

  def unfreeze(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerService.unfreeze(ip).map {
        case state.OrderCompleted(result,exitCode) => Ok(Json.toJson(ResultMessage("Computer unfreezed successfully", Seq(result))))
        case state.NotFound => NotFound
        case state.OrderFailed(result, exitCode) => BadRequest(Json.toJson(ResultMessage("Could not unfreeze that computer", Seq(result))))
        case _ => BadRequest
      }
  }

  def sendCommand(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      SSHOrderForm.form.bindFromRequest.fold(
        errorForm => {
          play.Logger.error(errorForm.toString)
          play.Logger.error(errorForm.errors.toString)
          Future.successful(BadRequest)//(index(messagesApi("sshorder.formerror"), notImplemented(messagesApi("sshorder.notimplemented")))))
        },
        data => {
          computerService.sendCommand(ip, data.superUser, data.command).map {
            case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(ResultMessage("Order sent successfully", Seq(result))))
            case state.NotFound => NotFound
            case state.OrderFailed(result, exitCode) => BadRequest(Json.toJson(ResultMessage("Could not send that command to that computer", Seq(result))))
            case _ => BadRequest
          }
        })
  }

  def sendMessage(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      MessageForm.form.bindFromRequest.fold(
        errorForm => {
          /*play.Logger.error(errorForm.toString)*/
          /*play.Logger.error(errorForm.errors.toString)*/
          Future.successful(BadRequest)//(index(messagesApi("message.formerror"), notImplemented(messagesApi("page.notimplemented")))))
        },
        data => {
          computerService.sendMessage(ip, data.message).map {
            case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(ResultMessage("Message sent successfully", Seq(result))))
            case state.NotFound => NotFound
            case state.NotCheckedYet => InternalServerError
            case _ => BadRequest
          }
        })
  }

  /**
    * Install a package on the remote computer
    *
    * @param ip       IP of the remote computer
    * @param programs Programs to be installed
    */
  def installAPackage(ip: String, programs: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerService.installAPackage(ip, programs).map {
        case state.OrderCompleted(_, _) => Ok
        case state.NotCheckedYet => InternalServerError("Computer not checked yet")
        case _ => BadRequest
      }
  }

}
