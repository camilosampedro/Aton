package controllers.api

import com.google.inject.Inject
import dao.{LaboratoryDAO, UserDAO}
import model.json.Writes._
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model._
import model.form.data.LoginFormData
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, Controller}
import services.LaboratoryService
import views.html._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 20/03/16.
  */
class LaboratoryController @Inject()(userDAO: UserDAO, laboratoryService: LaboratoryService, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val isAdmin: Boolean = true

  //override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def get(id: Long) = Action.async { implicit request =>
    Logger.debug("Petición de listar el laboratory " + id + " [API] respondida.")
    implicit val username = Some("")
    laboratoryService.get(id).map {
      case Some((laboratoryObject, roomsWithComputers)) => Ok(laboratoryObject.toString+roomsWithComputers.toString())
      case _ => NotImplemented(index(messagesApi("laboratory.notFound"),notImplemented(messagesApi("laboratory.notFound"))))
    }
  }

  def listAll() = Action.async {implicit request=>
    laboratoryService.listAll.map(result => {
      Ok(Json.toJson(result))
    })
  }
}
