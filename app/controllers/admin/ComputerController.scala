package controllers.admin

import com.google.inject.Inject
import com.jcraft.jsch.JSchException
import controllers.{routes => normalroutes}
import dao.{ComputerDAO, RoomDAO, UserDAO}
import model.Computer
import model.form._
import model.form.data.ComputerFormData
import play.Logger
import play.api.Environment
import play.api.i18n.MessagesApi
import services.state.Completed
import services.{ComputerService, SSHOrderService}
import views.html._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class ComputerController @Inject()(sSHOrderService: SSHOrderService, computerService: ComputerService, roomDAO: RoomDAO, val messagesApi: MessagesApi)(implicit userDAO: UserDAO, executionContext: ExecutionContext, environment: Environment) extends ControllerWithAuthRequired {
  def edit = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    ComputerForm.form.bindFromRequest().fold(
      errorForm => {
        computerService.getSingle(errorForm.get.ip).map {
          case Some(computer) =>
            computer.roomID match {
              case Some(roomID) =>
                Await.result(roomDAO.get(roomID), 5.seconds) match {
                  case Some(room) =>
                    val rooms = Await.result(roomDAO.getByLaboratory(room.id), 5.seconds)
                    val pairs = rooms.map(x => (x.id.toString, x.name))
                    Ok(index(messagesApi("computer.edit"), editComputer(errorForm, pairs)))
                  case _ =>
                    NotFound("Computer has not associated room")
                }
              case _ => val rooms = Await.result(roomDAO.listAll, 5.seconds)
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

  def add = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    Logger.debug("Add computer request received:" + request)
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

  def editForm(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      Logger.debug("Looking for computer: " + ip)
      val results = for {
        computerSearch <- computerService.getSingle(ip)
        roomsSearch <- roomDAO.listAll
      } yield (computerSearch, roomsSearch)
      results.map(res =>
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

  def addForm() = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      roomDAO.listAll.map {
        rooms =>
          val pairs = rooms.map(x => (x.id.toString, x.name))
          Ok(index(messagesApi("computer.add"), registerComputer(ComputerForm.form, pairs)))
      }
  }

  def delete(ip: String) = AuthRequiredAction {
    implicit request =>
      computerService.delete(ip) map {
        res =>
          Redirect(normalroutes.HomeController.home())
      }
  }

  def shutdown(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerService.shutdown(ip).map {
        case Completed => Redirect(normalroutes.HomeController.home())
        case _ => NotImplemented(index(messagesApi("computer.notFound"), notImplemented(messagesApi("computer.notFoundMessage"))))
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
            case Completed => Ok
            case _ => ServiceUnavailable
          }
          val computerTask = computerService.getSeveral(data.selectedComputers).map(_.map(sSHOrderService.shutdown))
          computerTask.map(result => Ok(index(messagesApi("done"), notImplemented("done"))))
        }
      )
  }

  def upgrade(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerService.getWithStatus(ip).map { computerWithStatuses =>
        computerWithStatuses.groupBy(_._1).headOption.map { computerStatus =>
          (computerStatus._1, computerStatus._2.flatMap(_._2).sortBy(_.registeredDate.getTime).headOption)
        } match {
          case Some((computer, Some(computerState))) =>
            val (result, success) = sSHOrderService.upgrade(computer, computerState)
            if (success) {
              NotImplemented(index(messagesApi("computer.upgrade.succeeded.title"), notImplemented(messagesApi("computer.upgrade.succeeded.body"))))
            } else {
              NotImplemented(index(messagesApi("computer.upgrade.failed"), notImplemented(messagesApi("computer.upgrade.failed") + result)))
            }
          case _ => NotImplemented(index(messagesApi("computer.notFound"), notImplemented(messagesApi("computer.notFoundMessage"))))
        }
      }
  }

  def unfreeze(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      computerService.getSingle(ip).map {
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

  def sendCommand(ip: String) = AuthRequiredAction {
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
          computerService.getSingle(ip).map {

            case Some(computer) =>
              val (result, exitstatus) = try {
                sSHOrderService.execute(computer, data.superUser, data.command)
              } catch {
                case e: JSchException => (e.getCause, 1)
                case e: Exception => ("Error no esperado: " + e.getCause, 1)
              }
              Ok(index(messagesApi("sshorder.executed"), notImplemented(messagesApi("sshorder.resulttext", result, exitstatus))))
            case _ => BadRequest(index(messagesApi("computer.notfound"), notImplemented(messagesApi("computer.notfound"))))
          }
        }
      )
  }

  def blockPage(ip: String) = AuthRequiredAction {
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

          computerService.getSingle(ip).map {
            case Some(computer) =>
              val (result, exitstatus) = sSHOrderService.blockPage(computer, data.page)
              Ok(index(messagesApi("page.done"), notImplemented(messagesApi("page.resulttext", result, exitstatus))))
            case _ => BadRequest(index(messagesApi("computer.notfound"), notImplemented(messagesApi("computer.notfound"))))
          }
        }
      )
  }

  def sendMessage(ip: String) = AuthRequiredAction {
    implicit request =>
      implicit val username = Some(loggedIn.username)
      implicit val user = loggedIn.username
      MessageForm.form.bindFromRequest.fold(
        errorForm => {
          /*play.Logger.error(errorForm.toString)*/
          /*play.Logger.error(errorForm.errors.toString)*/
          Future.successful(BadRequest(index(messagesApi("message.formerror"), notImplemented(messagesApi("page.notimplemented")))))
        },
        data => {
          computerService.sendMessage(ip, data.message).map {
            case services.state.Completed => Ok(index(messagesApi("message.done"), notImplemented(messagesApi("message.resulttext"))))
            case services.state.NotFound => BadRequest(index(messagesApi("computer.notfound"), notImplemented(messagesApi("computer.notfound"))))
            case services.state.Empty => Ok(index(messagesApi("message.emptycomputer"), notImplemented(messagesApi("message.emptycomputerbody"))))
          }
        }
      )
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
        case services.state.Completed => Ok
      }
  }


}
