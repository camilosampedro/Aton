package controllers

import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

/**
  * Created by camilosampedro on 10/05/16.
  */
class AdministrationController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def administrationPanel = Action.async{ implicit request =>
    Future.successful(Redirect(routes.LaboratoryController.listAll()))
  }

}
