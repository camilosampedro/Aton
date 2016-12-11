package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{LaboratoryDAO, RoomDAO, UserDAO}
import model.Role._
import model.Room
import model.form.{BlockUserForm, RoomForm}
import model.form.data.{BlockUserFormData, RoomFormData}
import play.Logger
import play.api.Environment
import play.api.i18n.MessagesApi
import services.{LaboratoryService, RoomService, UserService, state}
import views.html._

import scala.concurrent.ExecutionContext

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class RoomController @Inject()(roomService: RoomService, laboratoryService: LaboratoryService, val messagesApi: MessagesApi)(implicit userService: UserService, executionContext: ExecutionContext, environment: Environment) extends ControllerWithAuthRequired {

  def add = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    Logger.debug("Adding roomPanel... ")
    RoomForm.form.bindFromRequest().fold(
      errorForm => {
        Logger.error("There was an error with the input" + errorForm)
        laboratoryService.listAll.map { laboratories =>
          val pairs = laboratories.map(x => (x.id.toString, x.name))
          Ok//(index(messagesApi("room.add_room"),registerRoom(errorForm, pairs)))
        }
      },
      data => {
        val newRoom = Room(0, data.name, data.audiovisualResources, data.basicTools, data.laboratoryID)
        roomService.add(newRoom).map {
          case state.ActionCompleted => Redirect(normalroutes.HomeController.home())
          case _ => BadRequest
        }
      }
    )
  }

  def addForm() = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    laboratoryService.listAll.map { laboratories =>
      val pairs = laboratories.map(x => (x.id.toString, x.name))
      Ok//(index(messagesApi("room.add_room"),registerRoom(RoomForm.form, pairs)))
    }
  }

  def editForm(roomId: Long) = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    val results = for {
      roomResult <- roomService.get(roomId)
      laboratoriesResult <- laboratoryService.listAll
    } yield (roomResult, laboratoriesResult)
    results.map { res =>
      res._1 match {
        case Some(room) =>
          val roomFormData = RoomFormData(room.name, room.audiovisualResources, room.basicTools, room.laboratoryID)
          val pairs = res._2.map(x => (x.id.toString, x.name))
          Ok//(index(messagesApi("room.edit"),registerRoom(RoomForm.form.fill(roomFormData), pairs)))
        case _ =>
          NotImplemented(messagesApi("room.notFound"))
      }
    }
  }

  def blockUserForm(roomId: Long) = AuthRequiredAction { implicit request =>
    val results = for {
      roomResult <- roomService.get(roomId)
      usersResult <- userService.listAll
    } yield (roomResult, usersResult)
    results.map { res =>
      res._1 match {
        case Some(room) =>
          val blockUserFormData = BlockUserFormData("")
          val users = res._2.map(x => (x.username, x.name map {y => y} getOrElse x.username))
          Ok//(views.html.blockUser(BlockUserForm.form.fill(blockUserFormData), room.id, users))
        case _ =>
          NotImplemented(messagesApi("room.notFound"))
      }
    }
  }

  def edit = StackAction(AuthorityKey -> Administrator) { implicit request =>
    NotImplemented
  }

  def delete(roomId: Long) = AuthRequiredAction { implicit request =>
    roomService.delete(roomId).map {
      case state.ActionCompleted => Redirect(normalroutes.HomeController.home())
      case state.NotFound => NotFound
      case _ => BadRequest
    }
  }

  def blockUser(roomId: Long) = AuthRequiredAction { implicit request =>
    // TODO: Processing Not Yet Implemented
    val results = for {
      roomResult <- roomService.get(roomId)
    } yield roomResult
    results.map { result: Option[Room] =>
      if (result.isDefined)
        Redirect(normalroutes.LaboratoryController.get(result.get.laboratoryID))
      else
        NotFound
    }
  }
}
