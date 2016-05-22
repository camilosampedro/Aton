package controllers.api

import com.google.inject.Inject
import dao.{LaboratoryDAO, UserDAO}
import model.json.Writes._
import jp.t2v.lab.play2.auth.OptionalAuthElement
import model._
import model.form.data.LoginFormData
import play.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 20/03/16.
  */
class LaboratoryController @Inject()(userDAO: UserDAO, laboratoryDAO: LaboratoryDAO, val messagesApi: MessagesApi) extends Controller {

  //override def resolveUser(id: LoginFormData)(implicit context: ExecutionContext): Future[Option[User]] = userDAO.get(id)

  def get(id: Long) = Action.async { implicit request =>
    Logger.debug("Petición de listar el laboratory " + id + " [API] respondida.")
    laboratoryDAO.getWithChildren(id).map { res =>
      val grouped = res.groupBy(_._1)
      grouped.headOption match {
        case Some((laboratory, rooms)) =>
          val roomsWithComputers: Map[Option[Room], Seq[(Computer, ComputerState)]] = rooms.map {
            row => (row._2, row._3)
          }.groupBy {
            row => row._1
          }.map {
            case (k, v) =>
              (k, v
                .map(_._2)
                .groupBy(_._1)
                .filter(_._1.isDefined)
                .map(x => (x._1.get, x._2))
                .map { x =>
                  val filtered = x._2.filter(_._2.isDefined)
                  filtered.headOption.map { _ =>
                    filtered.maxBy(_._2.get.registeredDate.getTime)
                  }
                }.filter(x=>x.isDefined && x.get._1.isDefined && x.get._2.isDefined).map(x => (x.get._1.get, x.get._2.get)).toSeq)
          }.filter {
            row => row._1.isDefined
          }

          val json = Json.toJson(roomsWithComputers)
          Ok(json)
        case e =>
          NotFound("Laboratory not found")
      }

    }
  }

  def listAll() = Action.async {implicit request=>
    laboratoryDAO.listAll.map(result => {
      Ok(Json.toJson(result))
    })
  }
}
