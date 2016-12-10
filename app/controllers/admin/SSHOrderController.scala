package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{SSHOrderDAO, UserDAO}
import play.api.Environment
import play.api.i18n.MessagesApi
import services.{SSHOrderService, UserService, state}
import views.html._

import scala.concurrent.ExecutionContext

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class SSHOrderController @Inject()(sSHOrderService: SSHOrderService, val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext, userService: UserService, environment: Environment) extends ControllerWithAuthRequired {

  def listAll = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    sSHOrderService.listAll.map {res=>
      Ok//(index(messagesApi("sshorders"),sshOrders(res.take(20))))
    }
  }

  def get(id: Long) = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    sSHOrderService.get(id).map {res=>
      Ok//(index(messagesApi("sshorder"),sshOrder(res)))
    }
  }

  def delete(id: Long) = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    sSHOrderService.delete(id).map {
      case state.ActionCompleted => Ok("OK")
      case _ => BadRequest
    }
  }
}
