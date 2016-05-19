package controllers.admin

import com.google.inject.Inject
import controllers.{AuthConfigImpl, routes => normalroutes}
import dao.{ComputerDAO, RoomDAO, UserDAO}
import jp.t2v.lab.play2.auth.AuthElement
import model.Computer
import model.Role._
import model.form.data.{ComputerFormData, LoginFormData}
import model.form.{ComputerForm, ComputerFormPre}
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import services.{ComputerService, SSHOrderService}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by camilo on 7/05/16.
  */
class ComputerController @Inject()(userDAO: UserDAO, sSHOrderService: SSHOrderService, computerService: ComputerService, roomDAO: RoomDAO, computerDAO: ComputerDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport with AuthElement with AuthConfigImpl {
  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def edit = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    implicit val username = loggedIn.username
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
                      Ok(views.html.index(Some(username), true, messagesApi("computer.edit"))(views.html.editComputer(errorForm, pairs)))
                    case _ =>
                      NotFound("Computer has not asociated")
                  }
                }
                case _ => val rooms = Await.result(roomDAO.listAll, 5 seconds)
                  val pairs = rooms.map(x => (x.id.toString, x.laboratoryID + x.name))

                  val computerForm = ComputerFormData(computer.ip, computer.name, computer.SSHUser, computer.SSHPassword, computer.description, None)
                  Ok(views.html.index(Some(username), true, messagesApi("computer.edit"))(views.html.editComputer(ComputerForm.form.fill(computerForm), pairs)))

              }
              Ok
            case _ => NotFound("Computer not found")
          }
        }
      },
      data => {
        val newComputer = Computer(data.ip, data.name, data.SSHUser, data.SSHPassword, data.description, data.roomID)
        computerService.add(newComputer).map { res =>
          Redirect(normalroutes.HomeController.home())
        }
      }
    )
  }

  def add(laboratoryId: Long) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    implicit val username = loggedIn.username
    Logger.debug("Request de agregar equipo ingresada:" + request)
    ComputerFormPre.form.bindFromRequest.fold(
      errorForm => Future.successful(Ok(errorForm.toString)),
      data => {
        val newComputer = Computer(data.ip, None, data.SSHUser, data.SSHPassword, None, None)
        Logger.debug("Adding a new computer: " + newComputer)
        computerService.add(newComputer).map { res =>
          Redirect(routes.ComputerController.editForm(newComputer.ip))
        }
      }
    )
  }

  def editForm(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = loggedIn.username
      Logger.debug("Looking for computer: " + ip)
      val resultados = for {
        computerSearch <- computerDAO.get(ip)
        roomsSearch <- roomDAO.listAll
      } yield (computerSearch, roomsSearch)

      resultados.map(res =>
        res._1 match {
          case Some(Computer(ip, name, sSHUser, sSHPassword, description, room)) => {
            val computerForm = ComputerFormData(ip, name, sSHUser, sSHPassword, description, room)
            val pairs = res._2.map(x => (x.id.toString, x.name))
            Ok(views.html.index(Some(username), true, messagesApi("computer.edit"))(views.html.editComputer(ComputerForm.form.fill(computerForm), pairs)))
          }
          case _ =>
            Logger.debug("The computer was not found")
            NotFound("Computer not found")
        }
      )
  }

  def addForm(laboratoryId: Long) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      val username = loggedIn.username
      roomDAO.getByLaboratory(laboratoryId).map {
        rooms =>
          val pairs = rooms.map(x => (x.id.toString, x.name))
          Ok(views.html.index(Some(username), true, messagesApi("computer.add"))(views.html.registerComputer(ComputerFormPre.form, laboratoryId)))
      }
  }

  def delete(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      computerDAO.delete(ip) map {
        res =>
          Redirect(normalroutes.HomeController.home())
      }
  }

  def shutdown(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = loggedIn.username
      computerDAO.get(ip).map { res =>
        res match {
          case Some(computer) if (sSHOrderService.shutdown(computer)) => Redirect(normalroutes.HomeController.home())
          case _ => NotImplemented(views.html.index(Some(username), true, messagesApi("computer.notFound"))(views.html.notImplemented(messagesApi("computer.notFoundMessage"))))
        }
      }
  }

  def upgrade(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = loggedIn.username
      computerDAO.get(ip).map { res =>
        res match {
          case Some(computer) =>
            val (result, success) = sSHOrderService.upgrade(computer)
            if (success) {
              Redirect(normalroutes.HomeController.home())
            } else {
              NotImplemented(views.html.index(Some(username), true, messagesApi("computer.upgrade.failed"))(views.html.notImplemented(messagesApi("computer.upgrade.failed") + result)))
            }

          case _ => NotImplemented(views.html.index(Some(username), true, messagesApi("computer.notFound"))(views.html.notImplemented(messagesApi("computer.notFoundMessage"))))
        }
      }
  }

  def unfreeze(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = loggedIn.username
      computerDAO.get(ip).map { res =>
        res match {
          case Some(computer) =>
            val (result, success) = sSHOrderService.unfreeze(computer)
            if (success) {
              Redirect(normalroutes.HomeController.home())
            } else {
              NotImplemented(views.html.index(Some(username), true, messagesApi("computer.upgrade.failed"))(views.html.notImplemented(messagesApi("computer.upgrade.failed") + result)))
            }
          case _ => NotImplemented(views.html.index(Some(username), true, messagesApi("computer.notFound"))(views.html.notImplemented(messagesApi("computer.notFoundMessage"))))
        }
      }
  }

}
