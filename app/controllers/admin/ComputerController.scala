package controllers.admin

import com.google.inject.Inject
import com.jcraft.jsch.JSchException
import controllers.{AuthConfigImpl, routes => normalroutes}
import dao.{ComputerDAO, RoomDAO, UserDAO}
import jp.t2v.lab.play2.auth.AuthElement
import model.{Computer, ComputerState, SSHOrder}
import model.Role._
import model.form.data.{ComputerFormData, LoginFormData}
import model.form._
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import services.{ComputerService, SSHOrderService}
import views.html._

import scala.collection.immutable.Iterable
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by camilo on 7/05/16.
  */
class ComputerController @Inject()(userDAO: UserDAO, sSHOrderService: SSHOrderService, computerService: ComputerService, roomDAO: RoomDAO, computerDAO: ComputerDAO, val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext) extends Controller with I18nSupport with AuthElement with AuthConfigImpl {
  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  implicit val isAdmin = true

  def edit = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    implicit val username = Some(loggedIn.username)
    ComputerForm.form.bindFromRequest().fold(
      errorForm => {
        computerDAO.get(errorForm.get.ip).map {
          case Some(computer) =>
            computer.roomID match {
              case Some(roomID) => {
                Await.result(roomDAO.get(roomID), 5 seconds) match {
                  case Some(room) =>
                    val rooms = Await.result(roomDAO.getByLaboratory(room.id), 5 seconds)
                    val pairs = rooms.map(x => (x.id.toString, x.name))
                    Ok(index(messagesApi("computer.edit"), editComputer(errorForm, pairs)))
                  case _ =>
                    NotFound("Computer has not asociated")
                }
              }
              case _ => val rooms = Await.result(roomDAO.listAll, 5 seconds)
                val pairs = rooms.map(x => (x.id.toString, x.laboratoryID + x.name))

                val computerForm = ComputerFormData(computer.ip, computer.name, computer.SSHUser, computer.SSHPassword, computer.description, None)
                Ok(index(messagesApi("computer.edit"), editComputer(ComputerForm.form.fill(computerForm), pairs)))

            }
            Ok
          case _ => NotFound("Computer not found")
        }
      },
      data => {
        val newComputer = Computer(data.ip, data.name, data.SSHUser, data.SSHPassword, data.description, data.roomID)
        computerService.edit(newComputer).map { res =>
          Redirect(normalroutes.HomeController.home())
        }
      }
    )
  }

  def add = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    implicit val username = Some(loggedIn.username)
    Logger.debug("Request de agregar equipo ingresada:" + request)
    ComputerForm.form.bindFromRequest.fold(
      errorForm => Future.successful(Ok(errorForm.toString)),
      data => {
        val ips: Array[String] = data.ip.split(",")
        val names: Array[String] = data.name.getOrElse("").split(",")
        val futures = ips.zip(names).map { pair =>
          val newComputer = Computer(pair._1, Some(pair._2), data.SSHUser, data.SSHPassword, data.description, data.roomID)
          Logger.debug("Adding a new computer: " + newComputer)
          computerService.add(newComputer)
        }.toSeq
        Future.sequence(futures).map(res => Redirect(normalroutes.HomeController.home()))
      }
    )
  }

  def editForm(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      Logger.debug("Looking for computer: " + ip)
      val resultados = for {
        computerSearch <- computerDAO.get(ip)
        roomsSearch <- roomDAO.listAll
      } yield (computerSearch, roomsSearch)

      resultados.map(res =>
        res._1 match {
          case Some(Computer(`ip`, name, sSHUser, sSHPassword, description, room)) =>
            val computerForm = ComputerFormData(ip, name, sSHUser, sSHPassword, description, room)
            val pairs = res._2.map(x => (x.id.toString, x.name))
            Ok(index(messagesApi("computer.edit"), editComputer(ComputerForm.form.fill(computerForm), pairs)))
          case _ =>
            Logger.debug("The computer was not found")
            NotFound("Computer not found")
        }
      )
  }

  def addForm() = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      roomDAO.listAll.map {
        rooms =>
          val pairs = rooms.map(x => (x.id.toString, x.name))
          Ok(index(messagesApi("computer.add"), registerComputer(ComputerForm.form, pairs)))
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
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerDAO.get(ip).map {
        case Some(computer) if sSHOrderService.shutdown(computer) => Redirect(normalroutes.HomeController.home())
        case _ => NotImplemented(index(messagesApi("computer.notFound"), notImplemented(messagesApi("computer.notFoundMessage"))))
      }
  }

  def shutdownSeveral() = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      SelectComputersForm.form.bindFromRequest().fold(
        errorForm => Future.successful(BadRequest),
          data => {
            val computerTask=computerService.get(data.selectedComputers).map(_.map(sSHOrderService.shutdown))
            computerTask.map(result=>Ok(index(messagesApi("done"),notImplemented("done"))))
          }
      )
  }

  def upgrade(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerDAO.getWithStatus(ip).map { computerWithStatuses =>
        computerWithStatuses.groupBy(_._1).headOption.map { computerStatus =>
          (computerStatus._1,computerStatus._2.flatMap(_._2).sortBy(_.registeredDate.getTime).headOption)
        } match {
          case Some((computer,Some(computerState))) =>
            val (result,success)=sSHOrderService.upgrade(computer,computerState)
            if(success) {
              NotImplemented(index(messagesApi("computer.upgradesucceeded"),notImplemented(messagesApi("computer.upgradesucceededbody"))))
            } else {
              NotImplemented(index(messagesApi("computer.upgrade.failed"), notImplemented(messagesApi("computer.upgrade.failed") + result)))
            }
          case _ => NotImplemented(index(messagesApi("computer.notFound"), notImplemented(messagesApi("computer.notFoundMessage"))))
        }
      }
  }

  def unfreeze(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerDAO.get(ip).map {
        case Some(computer) =>
          val (result, success) = sSHOrderService.unfreeze(computer)
          if (success) {
            Redirect(normalroutes.HomeController.home())
          } else {
            NotImplemented(index(messagesApi("computer.upgrade.failed"), notImplemented(messagesApi("computer.upgrade.failed") + result)))
          }
        case _ => NotImplemented(index(messagesApi("computer.notFound"), notImplemented(messagesApi("computer.notFoundMessage"))))
      }
  }

  def sendCommand(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      SSHOrderForm.form.bindFromRequest.fold(
        errorForm => {
          play.Logger.error(errorForm.toString)
          play.Logger.error(errorForm.errors.toString)
          Future.successful(BadRequest(index(messagesApi("sshorder.formerror"), notImplemented(messagesApi("sshorder.notimplemented")))))
        },
        data => {
          computerDAO.get(ip).map {

            case Some(computer) =>
              val (result, exitstatus) = try {
                 sSHOrderService.execute(computer, data.superUser, data.command)
              } catch {
                case e: JSchException => (e.getCause,1)
                case e: Exception => ("Error no esperado: " + e.getCause,1)
              }
              Ok(index(messagesApi("sshorder.executed"), notImplemented(messagesApi("sshorder.resulttext", result, exitstatus))))
            case _ => BadRequest(index(messagesApi("computer.notfound"), notImplemented(messagesApi("computer.notfound"))))
          }
        }
      )
  }

  def blockPage(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      BlockPageForm.form.bindFromRequest.fold(
        errorForm => {
          play.Logger.error(errorForm.toString)
          play.Logger.error(errorForm.errors.toString)
          Future.successful(BadRequest(index(messagesApi("page.formerror"), notImplemented(messagesApi("page.notimplemented")))))
        },
        data => {

          computerDAO.get(ip).map {
            case Some(computer) =>
              val (result, exitstatus) = sSHOrderService.blockPage(computer,data.page)
              Ok(index(messagesApi("page.done"), notImplemented(messagesApi("page.resulttext", result, exitstatus))))
            case _ => BadRequest(index(messagesApi("computer.notfound"), notImplemented(messagesApi("computer.notfound"))))
          }
        }
      )
  }

  def sendMessage(ip: String) = AsyncStack(AuthorityKey -> Administrator) {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      MessageForm.form.bindFromRequest.fold(
        errorForm => {
          play.Logger.error(errorForm.toString)
          play.Logger.error(errorForm.errors.toString)
          Future.successful(BadRequest(index(messagesApi("message.formerror"), notImplemented(messagesApi("page.notimplemented")))))
        },
        data => {
          computerService.get(ip).map {
            case Some((computer,Some((_,connectedUsers)))) =>
              sSHOrderService.sendMessage(computer,data.message,connectedUsers)
              Ok(index(messagesApi("message.done"), notImplemented(messagesApi("message.resulttext"))))
            case Some((computer,_)) => Ok(index(messagesApi("message.emptycomputer"), notImplemented(messagesApi("message.emptycomputerbody"))))
            case _ => BadRequest(index(messagesApi("computer.notfound"), notImplemented(messagesApi("computer.notfound"))))
          }
        }
      )
  }



}
