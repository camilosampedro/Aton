package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{LaboratoryDAO, RoomDAO, UserDAO}
import model.Role._
import model.Room
import model.form.RoomForm
import model.form.data.RoomFormData
import play.Logger
import play.api.Environment
import play.api.i18n.MessagesApi
import views.html._

import scala.concurrent.ExecutionContext

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class RoomController @Inject()(roomDAO: RoomDAO, laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi)(implicit userDAO: UserDAO, executionContext: ExecutionContext, environment: Environment) extends ControllerWithAuthRequired {

  def add = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    Logger.debug("Adding roomPanel... ")
    RoomForm.form.bindFromRequest().fold(
      errorForm => {
        Logger.error("There was an error with the input" + errorForm)
        laboratoryDAO.listAll.map { laboratories =>
          val pairs = laboratories.map(x => (x.id.toString, x.name))
          Ok(index(messagesApi("room.add_room"),registerRoom(errorForm, pairs)))
        }
      },
      data => {
        val newRoom = Room(0, data.name, data.audiovisualResources, data.basicTools, data.laboratoryID)
        roomDAO.add(newRoom).map { res =>
          Redirect(normalroutes.HomeController.home())
        }
      }
    )
  }

  def addForm() = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    laboratoryDAO.listAll.map { laboratories =>
      val pairs = laboratories.map(x => (x.id.toString, x.name))
      Ok(index(messagesApi("room.add_room"),registerRoom(RoomForm.form, pairs)))
    }
  }

  def editForm(roomId: Long) = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    val results = for {
      roomResult <- roomDAO.get(roomId)
      laboratoriesResult <- laboratoryDAO.listAll
    } yield (roomResult, laboratoriesResult)
    results.map { res =>
      res._1 match {
        case Some(room) =>
          val roomFormData = RoomFormData(room.name, room.audiovisualResources, room.basicTools, room.laboratoryID)
          val pairs = res._2.map(x => (x.id.toString, x.name))
          Ok(index(messagesApi("room.edit"),registerRoom(RoomForm.form.fill(roomFormData), pairs)))
        case _ =>
          NotImplemented(messagesApi("room.notFound"))
      }
    }
  }

  def edit(roomId: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    NotImplemented
  }

  def delete(roomId: Long) = AuthRequiredAction { implicit request =>
    roomDAO.delete(roomId).map { res =>
      Redirect(normalroutes.HomeController.home())
    }
  }
}
