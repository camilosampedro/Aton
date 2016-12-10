package controllers

import com.google.inject.Inject
import dao.UserDAO
import jp.t2v.lab.play2.auth.LoginLogout
import model.form.LoginForm
import play.api.Environment
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class LoginController @Inject()(userService: UserService, val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext, override val cookieSecureOptionPlay: Environment) extends Controller with I18nSupport with LoginLogout with AuthConfigImpl {

  def loginForm = Action {
    Ok//(views.html.login(LoginForm.form))
  }

  def login = Action.async { implicit request =>
    LoginForm.form.bindFromRequest().fold(
      errorForm => Future.successful(BadRequest),//(views.html.login(errorForm))),
      data => {
        val results = for {
          searchUser <- resolveUser(data)
          goto <- gotoLoginSucceeded(data)
        } yield (searchUser, goto)
        results.map { res =>
          res._1 match {
            case Some(user) => res._2
            case _ => Forbidden("Login failed. Try again")
          }
        }

      }
    )
  }

  /**
    * A function that returns a `User` object from an `Id`.
    * You can alter the procedure to suit your application.
    */
  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = userService.checkAndGet(id)

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }
}
