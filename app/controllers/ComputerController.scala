package controllers

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{ComputerDAO, RoomDAO, UserDAO}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model.form.data.LoginFormData
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import services.{ComputerService, SSHOrderService}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 7/05/16.
  */
class ComputerController @Inject()(userDAO: UserDAO, sSHOrderService: SSHOrderService, computerService: ComputerService, roomDAO: RoomDAO, computerDAO: ComputerDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport with OptionalAuthElement with AuthConfigImpl {

  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)
}
