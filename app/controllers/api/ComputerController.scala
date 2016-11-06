package controllers.api

import com.google.inject.Inject
import dao.{ComputerDAO, UserDAO}
import model.json.ModelWrites._
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.state.ActionCompleted
import services.{ComputerService, LaboratoryService, SSHOrderService}
import views.html._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class ComputerController @Inject()(computerService: ComputerService, computerDAO: ComputerDAO, sSHOrderService: SSHOrderService, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def shutdown(ip: String) = Action.async {
    implicit request =>
      computerDAO.get(ip).map {
        case Some(computer) if sSHOrderService.shutdown(computer)("Scheduled Checker")==ActionCompleted => Ok(Json.parse(
          """
            {
               "answer":"shutted down"
            }
          """))
        case _ => NotFound(Json.parse(
          """
            {
               "answer":"not found"
            }
          """))
      }
  }
}
