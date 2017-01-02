package controllers

import com.google.inject.Inject
import dao.UserDAO
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model.json.LoginJson
import play.api.Environment
import play.api.i18n.I18nSupport
import play.api.mvc.Controller
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
abstract class ControllerWithNoAuthRequired @Inject()(implicit override val cookieSecureOptionPlay: Environment, userService: UserService) extends Controller with I18nSupport with OptionalAuthElement with AuthConfigImpl {
  override def resolveUser(receivedForm: LoginJson)(implicit context: ExecutionContext): Future[Option[User]] = {
    play.Logger.debug("Retrieving user: " + receivedForm)
    userService.checkAndGet(receivedForm.username,receivedForm.password)
  }
}
