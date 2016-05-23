package controllers.admin

import com.google.inject.Inject
import controllers.{AuthConfigImpl, routes => normalroutes}
import dao.{LaboratoryDAO, RoomDAO, SSHOrderDAO, UserDAO}
import jp.t2v.lab.play2.auth.AuthElement
import model.Role._
import model.Room
import model.form.RoomForm
import model.form.data.{LoginFormData, RoomFormData}
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import views.html._

import scala.concurrent.{ExecutionContext, Future}
import views.html._

/**
  * Created by camilosampedro on 11/05/16.
  */
class SSHOrderController @Inject()(userDAO: UserDAO, sSHOrderDAO: SSHOrderDAO, val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext) extends Controller with I18nSupport with AuthElement with AuthConfigImpl {

  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  implicit val isAdmin = true

  def listAll = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    implicit val username = Some(loggedIn.username)
    sSHOrderDAO.listAll.map {res=>
      Ok(index(messagesApi("sshorders"),sshOrders(res.take(20))))
    }
  }

  def get(id: Long) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    implicit val username = Some(loggedIn.username)
    sSHOrderDAO.get(id).map {res=>
      Ok(index(messagesApi("sshorder"),sshOrder(res)))
    }
  }

  def delete(id: Long) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    implicit val username = Some(loggedIn.username)
    sSHOrderDAO.delete(id).map {res=>
      Ok("OK")
    }
  }
}
