package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import model.Computer
import model.form._
import model.form.data.ComputerFormData
import model.json.{ComputerJson, ResultMessage}
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, Result}
import services._
import services.state.ActionState
import views.html._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class ComputerController @Inject()(computerService: ComputerService, roomService: RoomService, val messagesApi: MessagesApi)
                                  (implicit userService: UserService, executionContext: ExecutionContext, environment: Environment)
  extends ControllerWithAuthRequired {

  def ipAction(action: (List[String], String) => Future[ActionState])(mapping: ActionState => Result) =
    AuthRequiredAction { implicit request =>
      implicit val user = loggedIn.username
      request.body.asJson match {
        case Some(json) =>
          val ip = (json \\ "ip").map(_.as[String])
          action(ip.toList, user).map(mapping)
        case _ => Future.successful(BadRequest(Json.toJson(new ResultMessage("Non json not expected"))))
      }
    }

  def add = AuthRequiredAction { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        json.validate[ComputerJson] match {
          case JsSuccess(computer, path) =>
            computerService.add(computer.ip, computer.name, computer.SSHUser, computer.SSHPassword,
              computer.description, computer.roomID).map {
              case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computer added successfully")))
              case _ => BadRequest(Json.toJson(new ResultMessage("Could not add that computer")))
            }
          case JsError(errors) =>
            Future.successful(BadRequest(Json.toJson(ResultMessage("Could not add that computer", errors.map(_.toString)))))
        }
      case _ => Future.successful(BadRequest(Json.toJson(new ResultMessage("Non json not expected"))))
    }
  }

  def blockPage = AuthRequiredAction {
    implicit request =>
      implicit val user = loggedIn.username
      request.body.asJson match {
        case Some(json) =>
          val ips = (json \ "ips").as[List[String]]
          val page = (json \ "page").as[String]
          computerService.blockPage(ips, page).map {
            case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(ResultMessage("Page blocked successfully on the computer", Seq(exitCode.toString, result))))
            case state.NotFound => NotFound(Json.toJson(new ResultMessage("Computer not found")))
            case state.OrderFailed(result, exitCode) => BadRequest(Json.toJson(ResultMessage("Could not block that page", Seq(result))))
            case _ => BadRequest(Json.toJson(new ResultMessage("Could not block that page")))
          }
        case _ => Future.successful(BadRequest(Json.toJson(new ResultMessage("Non json not expected"))))
      }
  }

  def delete(ip: String) = AuthRequiredAction {
    implicit request =>
      computerService.delete(ip) map {
        case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computer deleted successfully")))
        case state.NotFound => NotFound(Json.toJson(new ResultMessage("Computer not found")))
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
                Ok //(index(messagesApi("computer.edit"), editComputer(errorForm, pairs)))
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

  def shutdown: Action[AnyContent] = ipAction(computerService.shutdown(_)(_)) {
    case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computer shutdown successfully")))
    case state.NotFound => NotFound(Json.toJson(new ResultMessage("Could not find that computer")))
    case _ => BadRequest(Json.toJson(new ResultMessage("Could not shutdown that computer")))
  }

  def upgrade: Action[AnyContent] = ipAction(computerService.upgrade(_)(_)) {
    case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computer upgraded successfully")))
    case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(new ResultMessage("Computer upgraded successfully")))
    case state.NotFound => NotFound(Json.toJson(new ResultMessage("Could not find that computer")))
    case _ => BadRequest(Json.toJson(new ResultMessage("Could not upgrade that computer")))
  }

  def unfreeze: Action[AnyContent] = ipAction(computerService.unfreeze(_)(_)) {
    case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(ResultMessage("Computer unfreezed successfully", Seq(result))))
    case state.NotFound => NotFound
    case state.OrderFailed(result, exitCode) => BadRequest(Json.toJson(ResultMessage("Could not unfreeze that computer", Seq(result))))
    case _ => BadRequest(Json.toJson(new ResultMessage("Could not unfreeze that computer")))
  }

  def sendCommand = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      val ip = List("")
      SSHOrderForm.form.bindFromRequest.fold(
        errorForm => {
          play.Logger.error(errorForm.toString)
          play.Logger.error(errorForm.errors.toString)
          Future.successful(BadRequest) //(index(messagesApi("sshorder.formerror"), notImplemented(messagesApi("sshorder.notimplemented")))))
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

  def sendMessage = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      val ip = List("")
      MessageForm.form.bindFromRequest.fold(
        errorForm => {
          /*play.Logger.error(errorForm.toString)*/
          /*play.Logger.error(errorForm.errors.toString)*/
          Future.successful(BadRequest) //(index(messagesApi("message.formerror"), notImplemented(messagesApi("page.notimplemented")))))
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
    */
  def installAPackage = AuthRequiredAction {
    implicit request =>
      // TODO: Receive programs and ip
      val programs = ""
      val ip = ""
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerService.installAPackage(ip, programs).map {
        case state.OrderCompleted(_, _) => Ok
        case state.NotCheckedYet => InternalServerError("Computer not checked yet")
        case _ => BadRequest
      }
  }

}
