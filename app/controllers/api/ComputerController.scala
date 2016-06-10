package controllers.api

import com.google.inject.Inject
import dao.{ComputerDAO, UserDAO}
import model.json.Writes._
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{ComputerService, LaboratoryService, SSHOrderService}
import views.html._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by camilo on 20/03/16.
  */
class ComputerController @Inject()(computerService: ComputerService, computerDAO: ComputerDAO, sSHOrderService: SSHOrderService, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def shutdown(ip: String) = Action.async {
    implicit request =>
      computerDAO.get(ip).map {
        case Some(computer) if sSHOrderService.shutdown(computer)("API") => Ok(Json.parse(
          """
            |{
            |   "answer":"apagado"
            |}
          """))
        case _ => NotFound(Json.parse(
          """
            |{
            |   "answer":"no encontrado"
            |}
          """))
      }
  }
}
