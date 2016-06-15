package controllers.admin

import com.google.inject.Inject
import controllers.{AuthConfigImpl, routes => normalroutes}
import dao.{LaboratoryDAO, RoomDAO, SuggestionDAO, UserDAO}
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

/**
  * Created by camilosampedro on 11/05/16.
  */
class SuggestionController @Inject()(userDAO: UserDAO, suggestionDAO: SuggestionDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport with AuthElement with AuthConfigImpl {

  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  implicit val isAdmin = true

  def delete(id: Long) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    suggestionDAO.delete(id).map { res =>
      Redirect(normalroutes.SuggestionController.home())
    }
  }
}
