package config

import javax.inject._

import akka.actor._
import dao.{ComputerStateDAO, ConnectedUserDAO}
import model.{ComputerState, ConnectedUser}
import services.{ComputerService, SSHOrderService, Timer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * This has the task of checking periodically the computers' state, without blocking the main thread.
  *
  * @param connectedUserDAO Injected.
  * @param computerStateDAO Injected.
  * @param computerService  Injected.
  * @param sSHOrderService  Injected.
  * @param actorSystem      Injected.
  * @param executionContext Injected.
  */
class ComputerChecker @Inject()(connectedUserDAO: ConnectedUserDAO, computerStateDAO: ComputerStateDAO, computerService: ComputerService, sSHOrderService: SSHOrderService, actorSystem: ActorSystem, executionContext: ExecutionContext) extends UntypedActor with Timer {

  // Is the checker actually executing.
  var isExecuting = false

  /**
    * Execute the checker task.
    *
    * @param message Not needed.
    */
  @scala.throws[Exception](classOf[Exception])
  override def onReceive(message: Any): Unit = {
    // Only execute when it's not executing.
    if (!isExecuting) {

      // Set flag. It starts to execute now.
      isExecuting = true

      play.Logger.info("Executing computer checker.")

      // Create a checker task for every computer on the database.
      val task = computerService.listAllSimple.map { computers =>
        time {
          computers.map { computer =>
            play.Logger.debug("Checking: " + computer)
            sSHOrderService.check(computer)("Scheduled Checker")
          }
        }
      }

      // Execute all the task collected in the last step.
      val results: Seq[(ComputerState, Seq[ConnectedUser])] = Await.result(task, Duration.Inf)
      play.Logger.debug(s"""Computers checked, proceeding to save: $results""")

      // Save every result on the database.
      for (result <- results) {
        val computerState = result._1
        val addComputerStateTask = computerStateDAO.add(computerState)
        Await.result(addComputerStateTask, Duration.Inf)
        val connectedUsers = result._2
        val addConnectedUsersTasks = connectedUsers.map {
          connectedUserDAO.add
        }
        val f = Future.sequence(addConnectedUsersTasks.toList)
        Await.result(f, Duration.Inf)
      }
      // Reset the execution flag.
      isExecuting = false
    } else {
      // It is now executing
      play.Logger.debug("Already executing computer checker. omiting")
    }
  }


}