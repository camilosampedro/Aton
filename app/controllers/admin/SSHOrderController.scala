package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{SSHOrderDAO, UserDAO}
import play.api.Environment
import play.api.i18n.MessagesApi
import views.html._

import scala.concurrent.ExecutionContext

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class SSHOrderController @Inject()(sSHOrderDAO: SSHOrderDAO, val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext, userDAO: UserDAO , environment: Environment) extends ControllerWithAuthRequired {

  def listAll = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    sSHOrderDAO.listAll.map {res=>
      Ok(index(messagesApi("sshorders"),sshOrders(res.take(20))))
    }
  }

  def get(id: Long) = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    sSHOrderDAO.get(id).map {res=>
      Ok(index(messagesApi("sshorder"),sshOrder(res)))
    }
  }

  def delete(id: Long) = AuthRequiredAction { implicit request =>
    implicit val username = Some(loggedIn.username)
    sSHOrderDAO.delete(id).map {res=>
      Ok("OK")
    }
  }
}
