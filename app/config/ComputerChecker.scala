package config

import javax.inject._

import akka.actor._
import dao.{ComputerDAO, ComputerStateDAO, ConnectedUserDAO}
import model.{ComputerState, ConnectedUser}
import services.{ComputerService, SSHOrderService}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class ComputerChecker @Inject()(connectedUserDAO: ConnectedUserDAO, computerStateDAO: ComputerStateDAO, computerService: ComputerService, sSHOrderService: SSHOrderService, actorSystem: ActorSystem) extends UntypedActor {
  @scala.throws[Exception](classOf[Exception])
  override def onReceive(message: Any): Unit =  {
    play.Logger.debug("Executing computer checker.")
    val task = computerService.listAll.map{ computers =>
      computers.map{ computer =>
        play.Logger.debug("Checking: " + computer)
        sSHOrderService.check(computer._1,computer._2)("Scheduled Checker")
      }
    }
    val results: Seq[(ComputerState, Seq[ConnectedUser])] = Await.result(task,Duration.Inf)
    for (result <- results) {
      val computerState = result._1
      val addComputerStateTask = computerStateDAO.add(computerState)
      Await.result(addComputerStateTask,Duration.Inf)
      val connectedUsers = result._2
      val addConnectedUsersTasks = connectedUsers.map{connectedUserDAO.add}
      val f = Future.sequence(addConnectedUsersTasks.toList)
      Await.result(f,Duration.Inf)
    }
  }
}