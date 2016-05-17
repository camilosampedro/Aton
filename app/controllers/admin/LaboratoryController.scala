package controllers.admin

import com.google.inject.Inject
import controllers.{AuthConfigImpl, routes => normalroutes}
import dao.{LaboratoryDAO, UserDAO}
import jp.t2v.lab.play2.auth.AuthElement
import model.Role._
import model.form.LaboratoryForm
import model.form.data.{LaboratoryFormData, LoginFormData}
import model.{Laboratory, Role}
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 20/03/16.
  */
class LaboratoryController @Inject()(userDAO: UserDAO, laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport with AuthElement with AuthConfigImpl {

  override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def administrateLaboratories = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    Logger.debug("Petición de listar los laboratorios administrativamente recibida.")
    Future.successful(Redirect(normalroutes.HomeController.home()))
  }

  def edit(id: Long) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    val username = loggedIn.username
    val isAdmin = loggedIn.role == Role.Administrator
    laboratoryDAO.get(id).map { laboratory =>
      laboratory match {
        case Some(laboratory) =>
          val data = LaboratoryFormData(laboratory.name, laboratory.location, laboratory.administration)
          Ok(views.html.index(Some(username), isAdmin, messagesApi("laboratory.edit"))(views.html.registerLaboratory(LaboratoryForm.form.fill(data))))
        case e => NotFound("Laboratory not found")
      }

    }
  }

  def add = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    Logger.debug("Adding laboratory... ")
    val username = loggedIn.username
    val isAdmin = loggedIn.role == Role.Administrator
    LaboratoryForm.form.bindFromRequest.fold(
      errorForm => {
        Logger.error("There was an error with the input" + errorForm)
        Future.successful(Ok(views.html.index(Some(username), isAdmin, messagesApi("laboratory.add"))(views.html.registerLaboratory(errorForm))))
      },
      data => {

        val newLaboratory = Laboratory(0, data.name, data.location, data.administration)
        laboratoryDAO.add(newLaboratory).map(res =>
          Redirect(normalroutes.HomeController.home())
        )
      }
    )
  }

  def addForm = StackAction(AuthorityKey -> Administrator) { implicit request =>
    play.Logger.debug("Logged user: " + loggedIn)
    val username = loggedIn.username
    val isAdmin = loggedIn.role == Role.Administrator
    Ok(views.html.index(Some(username), isAdmin, "Add laboratory")(views.html.registerLaboratory(LaboratoryForm.form)))
  }

  def delete(id: Long) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    laboratoryDAO.delete(id) map { res =>
      Redirect(normalroutes.HomeController.home())
    }
  }
}
