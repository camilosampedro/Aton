package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{SSHOrderDAO, UserDAO}
import play.api.i18n.MessagesApi
import views.html._

import scala.concurrent.ExecutionContext

/**
  * Created by camilosampedro on 11/05/16.
  */
class SSHOrderController @Inject()(sSHOrderDAO: SSHOrderDAO, val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext, userDAO: UserDAO) extends ControllerWithAuthRequired {

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
