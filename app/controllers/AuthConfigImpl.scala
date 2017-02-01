package controllers

import jp.t2v.lab.play2.auth._
import model.json.{LoginJson, ResultMessage}
import play.api.Mode
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.{ClassTag, classTag}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
trait AuthConfigImpl extends AuthConfig {
  /**
    * User information is of type LoginFormData
    */
  type Id = LoginJson

  /**
    * User class is User
    */
  type User = model.User

  val cookieSecureOptionPlay: play.api.Environment

  /**
    * Authority is represented by an Int.
    */
  type Authority = Int
  /**
    * (Optional)
    * You can custom SessionID Token handler.
    * Default implementation use Cookie.
    */
  override lazy val tokenAccessor = new CookieTokenAccessor(
    /*
     * Whether use the secure option or not use it in the cookie.
     * Following code is default.
     */
    cookieSecureOption = cookieSecureOptionPlay.mode match {
      case Mode.Prod =>
        play.Logger.debug("Using secure cookie option, it's in production mode")
        false //Set true if using encrypted connection
      case _ =>
        play.Logger.debug("Using testing cookie option, it's in development mode")
        false
    },
    cookieMaxAge = Some(sessionTimeoutInSeconds)
  )
  /**
    * A `ClassTag` is used to retrieve an id from the Cache API.
    * Use something like this:
    */
  val idTag: ClassTag[Id] = classTag[Id]
  /**
    * The session timeout in seconds.
    */
  val sessionTimeoutInSeconds = 3600

  /**
    * Where to redirect the user after a successful login.
    */
  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    play.Logger.info("Login succeeded")
    Future.successful(Ok(Json.toJson(new ResultMessage("Logged in successfully"))))
  }

  /**
    * Where to redirect the user after logging out.
    */
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    play.Logger.info("Logout succeeded")
    Future.successful(Ok(Json.toJson(new ResultMessage("Logged out successfully"))))
  }

  /**
    * If the user is not logged in and tries to access a protected resource then redirect them as follows:
    */
  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    play.Logger.warn("Authentication failed")
    Future.successful(Forbidden(Json.toJson(new ResultMessage("Authentication failed"))))
  }

  /**
    * If authorization failed (usually incorrect password) redirect the user as follows:
    */
  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] = {
    play.Logger.warn("Authorization failed")
    Future.successful(Unauthorized(Json.toJson(new ResultMessage("Unaouthorized"))))
  }

  /**
    * A function that determines what `Authority` a user has.
    * You should alter this procedure to suit your application.
    */
  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    import model.Role._
    play.Logger.debug("Authenticating  [ User: " + user + ", Authority: " + authority)
    (user.role, authority) match {
      case (Administrator, _) => true
      case (NormalUser, NormalUser) => true
      case _ => false
    }
  }

}
