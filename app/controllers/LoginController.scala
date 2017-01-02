package controllers

import com.google.inject.Inject
import dao.UserDAO
import jp.t2v.lab.play2.auth.LoginLogout
import model.form.LoginForm
import model.json.{LoginJson, ResultMessage}
import play.api.Environment
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class LoginController @Inject()(userService: UserService, val messagesApi: MessagesApi)(implicit executionContext: ExecutionContext, override val cookieSecureOptionPlay: Environment) extends Controller with I18nSupport with LoginLogout with AuthConfigImpl {

  def loginForm = Action {
    Ok//(views.html.login(LoginForm.form))
  }

  def login: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        json.validate[LoginJson] match {
          case JsSuccess(userForm, _ ) =>
            val result = for {
              foundUser <- resolveUser(userForm)
              goto <- gotoLoginSucceeded(userForm)
            } yield (foundUser, goto)
            result.map{
              case (Some(user),goto) => goto
              case _ => Forbidden(Json.toJson(new ResultMessage("Wrong username or password")))
            }
          case JsError(errors) =>
            Future.successful(BadRequest(Json.toJson(ResultMessage("Non json not expected", errors.map(_._2.toString)))))
        }
      case _ => Future.successful(BadRequest(Json.toJson(new ResultMessage("Non json not expected"))))
    }
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
