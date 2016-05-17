package controllers.admin

import com.google.inject.Inject
import controllers.{AuthConfigImpl, routes => normalroutes}
import dao.UserDAO
import jp.t2v.lab.play2.auth.AuthElement
import model.Role._
import model.form.data.LoginFormData
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilosampedro on 10/05/16.
  */
class AdministrationController @Inject()(val messagesApi: MessagesApi, userDAO: UserDAO) extends Controller with I18nSupport with AuthElement with AuthConfigImpl {

  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def administrationPanel = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    Future.successful(Redirect(normalroutes.HomeController.home()))
  }

}
