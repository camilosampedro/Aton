package controllers

import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

/**
  * Created by camilosampedro on 10/05/16.
  */
class AdministrationController extends Controller {
  def administrationPanel = Action.async{ implicit request =>
    Future.successful(Redirect(routes.LaboratoryController.listAll()))
  }

}
