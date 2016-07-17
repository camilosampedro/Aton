package controllers.admin

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.UserDAO
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilosampedro on 10/05/16.
  */
class AdministrationController @Inject()(val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext, userDAO: UserDAO) extends ControllerWithAuthRequired {
  def administrationPanel = AuthRequiredAction { implicit request =>
    Future.successful(Redirect(normalroutes.HomeController.home()))
  }
}
