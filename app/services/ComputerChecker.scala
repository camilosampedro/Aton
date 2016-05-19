package services
import play.api.mvc._
import akka.actor._
import javax.inject._

import dao.{ComputerDAO, ComputerStateDAO}
import scala.concurrent.ExecutionContext.Implicits.global

class ComputerChecker @Inject()(computerDAO: ComputerDAO, sSHOrderService: SSHOrderService) extends UntypedActor {
  @scala.throws[Exception](classOf[Exception])
  override def onReceive(message: Any): Unit =  {
    computerDAO.listAll.map{ computers =>
      for(computer <- computers) {
        sSHOrderService.check(computer)("Scheduled Checker")
      }
    }
  }
}