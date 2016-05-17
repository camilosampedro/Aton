package controllers.admin

import com.google.inject.Inject
import controllers.{AuthConfigImpl, routes => normalroutes}
import dao.{LaboratoryDAO, RoomDAO, UserDAO}
import jp.t2v.lab.play2.auth.AuthElement
import model.Role._
import model.Room
import model.form.RoomForm
import model.form.data.{LoginFormData, RoomFormData}
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilosampedro on 11/05/16.
  */
class RoomController @Inject()(userDAO: UserDAO, roomDAO: RoomDAO, laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport with AuthElement with AuthConfigImpl {

  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def add = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    val username = loggedIn.username
    Logger.debug("Addig roomPanel... ")
    RoomForm.form.bindFromRequest().fold(
      errorForm => {
        Logger.error("There was an error with the input" + errorForm)
        laboratoryDAO.listAll.map { laboratories =>
          val pairs = laboratories.map(x => (x.id.toString, x.name))
          Ok(views.html.index(Some(username), true, messagesApi("room.add"))(views.html.registerRoom(errorForm, pairs)))
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

  def addForm = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    val username = loggedIn.username
    laboratoryDAO.listAll.map { laboratories =>
      val pairs = laboratories.map(x => (x.id.toString, x.name))
      Ok(views.html.index(Some(username), true, messagesApi("room.add"))(views.html.registerRoom(RoomForm.form, pairs)))
    }
  }

  def editForm(roomId: Long) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    val username = loggedIn.username
    val results = for {
      roomResult <- roomDAO.get(roomId)
      laboratoriesResult <- laboratoryDAO.listAll
    } yield (roomResult, laboratoriesResult)
    results.map { res =>
      res._1 match {
        case Some(room) => {
          val roomFormData = RoomFormData(room.name, room.audiovisualResources, room.basicTools, room.laboratoryID)
          val pairs = res._2.map(x => (x.id.toString, x.name))
          Ok(views.html.index(Some(username), true, messagesApi("room.edit"))(views.html.registerRoom(RoomForm.form.fill(roomFormData), pairs)))
        }
        case _ => {
          NotImplemented(messagesApi("room.notFound"))
        }
      }
    }
  }

  def edit(roomId: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    NotImplemented
  }

  def delete(roomId: Long) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    roomDAO.delete(roomId).map { res =>
      Redirect(normalroutes.HomeController.home())
    }
  }
}
