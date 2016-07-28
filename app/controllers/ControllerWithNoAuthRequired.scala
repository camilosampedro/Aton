package controllers

import com.google.inject.Inject
import dao.UserDAO
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model.form.data.LoginFormData
import play.api.i18n.I18nSupport
import play.api.mvc.Controller

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
abstract class ControllerWithNoAuthRequired @Inject()(implicit userDAO: UserDAO) extends Controller with I18nSupport with OptionalAuthElement with AuthConfigImpl {
  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)
}
