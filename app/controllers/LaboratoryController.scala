package controllers

import com.google.inject.Inject
import dao.{LaboratoryDAO, UserDAO}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model.Role
import model.form.data.LoginFormData
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 20/03/16.
  */
class LaboratoryController @Inject()(userDAO: UserDAO, laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport with OptionalAuthElement with AuthConfigImpl {

  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def get(id: Long) = AsyncStack { implicit request =>
    play.Logger.debug("Logged user: " + loggedIn)
    val (username: Option[String], isAdmin: Boolean) = loggedIn match {
      case Some(user) => (Some(user.username), user.role == Role.Administrator)
      case _ => (None, false)
    }
    Logger.debug("Petición de listar el laboratorio " + id + " respondida.")
    laboratoryDAO.getWithChildren(id).map { res =>
      val grouped = res.groupBy(_._1)
      grouped.headOption match {
        case Some((laboratory, rooms)) => {
          val roomsWithComputers = rooms.map {
            row => (row._2, row._3)
          }.groupBy {
            row => row._1
          }.map {
            case (k, v) => (k, v.map(_._2).flatten)
          }.filter {
            row => row._1.isDefined
          }
          Ok(views.html.index(username, isAdmin, "Laboratory" + laboratory.name)(views.html.laboratory(laboratory, roomsWithComputers, isAdmin)))
        }
        case e => {
          NotFound("Laboratory not found")
        }
      }

    }
  }

}
