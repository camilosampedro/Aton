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
        case _ => Future.successful(BadRequest(Json.toJson(ResultMessage.inputWasNotAJson)))
      }
    }

  def add = AuthRequiredAction { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        json.validate[ComputerJson] match {
          case JsSuccess(computer, path) =>
            play.Logger.debug(s"Adding $computer")
            computerService.add(computer.ip, computer.name, computer.SSHUser, computer.SSHPassword,
              computer.description, computer.roomID).map {
              case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computer added successfully")))
              case _ => BadRequest(Json.toJson(new ResultMessage("Could not add that computer")))
            }
          case JsError(errors) =>
            Future.successful(BadRequest(Json.toJson(ResultMessage.wrongJsonFormat(errors))))
        }
      case _ => Future.successful(BadRequest(Json.toJson(ResultMessage.inputWasNotAJson)))
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
            case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(ResultMessage("Page blocked successfully on the computer", Seq(("exitCode", exitCode.toString), ("result", result)))))
            case state.NotFound => NotFound(Json.toJson(new ResultMessage("Computer not found")))
            case state.OrderFailed(result, exitCode) => BadRequest(Json.toJson(ResultMessage("Could not block that page", Seq(("result", result)))))
            case _ => BadRequest(Json.toJson(new ResultMessage("Could not block that page")))
          }
        case _ => Future.successful(BadRequest(Json.toJson(ResultMessage.inputWasNotAJson)))
      }
  }

  def delete(ip: String) = AuthRequiredAction {
    implicit request =>
      computerService.delete(ip) map {
        case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computer deleted successfully")))
        case state.NotFound => NotFound(Json.toJson(new ResultMessage("Computer not found")))
        case _ => BadRequest(Json.toJson(new ResultMessage("Could not deleteLaboratory that computer")))
      }
  }

  def edit = AuthRequiredAction { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        json.validate[Computer] match {
          case JsSuccess(computer, _) =>
            computerService.edit(computer).map {
              case state.ActionCompleted => Ok(Json.toJson(new ResultMessage("Computer updated successfully")))
              case _ => BadRequest(Json.toJson(new ResultMessage("Could not update that computer")))
            }
          case JsError(errors) =>
            Future.successful(BadRequest(Json.toJson(ResultMessage.wrongJsonFormat(errors))))
        }
      case _ => Future.successful(BadRequest(Json.toJson(ResultMessage.inputWasNotAJson)))
    }
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
    case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(ResultMessage("Computer unfreezed successfully", Seq(("result",result)))))
    case state.NotFound => NotFound
    case state.OrderFailed(result, exitCode) => BadRequest(Json.toJson(ResultMessage("Could not unfreeze that computer", Seq(("result",result)))))
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
            case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(ResultMessage("Order sent successfully", Seq(("result",result)))))
            case state.NotFound => NotFound
            case state.OrderFailed(result, exitCode) => BadRequest(Json.toJson(ResultMessage("Could not send that command to that computer", Seq(("result",result)))))
            case _ => BadRequest
          }
        })
  }

  def sendMessage = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      val ip = List("")
      request.body.asJson match {
        case Some(json) => play.Logger.info(json.toString())
          val text = (json \ "text").as[String]
          val ips = (json \ "ips").as[List[String]]
          play.Logger.info(s"IPS: $ips | text: $text")
          computerService.sendMessage(ips, text).map {
            case state.OrderCompleted(result, exitCode) => Ok(Json.toJson(ResultMessage("Message sent successfully", Seq(("result",result)))))
            case state.NotFound => NotFound(Json.toJson(new ResultMessage("Computer not found")))
            case state.NotCheckedYet => InternalServerError(Json.toJson(new ResultMessage("That computer was not checked yet")))
            case _ => BadRequest(Json.toJson(new ResultMessage("There was an error sending the message")))
          }
        case _ => Future.successful(BadRequest(Json.toJson(new ResultMessage("Non json not expected"))))
      }
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
