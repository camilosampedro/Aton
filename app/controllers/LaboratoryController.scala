package controllers

import com.google.inject.Inject
import dao.{LaboratoryDAO, UserDAO}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model.Role
import model.form.data.LoginFormData
import play.api.Logger
import views.html._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import services.LaboratoryService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 20/03/16.
  */
class LaboratoryController @Inject()(laboratoryService: LaboratoryService, userDAO: UserDAO, val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext) extends Controller with I18nSupport with OptionalAuthElement with AuthConfigImpl {

  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def get(id: Long) = AsyncStack { implicit request =>
    implicit val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(user) => (Some(user.username), user.role == Role.Administrator)
      case _ => (None, false)
    }
    laboratoryService.get(id).map {
      case Some((laboratoryObject, roomsWithComputers)) => Ok(index("Laboratory" + laboratoryObject.name,laboratory(laboratoryObject, roomsWithComputers)))
      case _ => NotImplemented(index(messagesApi("laboratory.notFound"),notImplemented(messagesApi("laboratory.notFound"))))
    }
  }

}
