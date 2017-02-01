package controllers.admin

import com.google.inject.Inject
import controllers.AuthConfigImpl
import dao.UserDAO
import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import model.Role._
import model.json.LoginJson
import play.api.Environment
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Controller, Result}
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
abstract class ControllerWithAuthRequired @Inject()(implicit userService: UserService, override val cookieSecureOptionPlay: Environment)
  extends Controller with I18nSupport with AuthElement with AuthConfigImpl {
  implicit val isAdmin = true

  override def resolveUser(receivedForm: LoginJson)(implicit context: ExecutionContext): Future[Option[User]] =
    userService.checkAndGet(receivedForm.username,receivedForm.password)

  def AuthRequiredAction: ((RequestWithAttributes[AnyContent]) => Future[Result]) => Action[AnyContent] =
    AsyncStack(AuthorityKey -> Administrator)(_)
}
