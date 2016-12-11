package services.impl

import com.google.inject.{Inject, Singleton}
import dao.ComputerDAO
import model.{Computer, ComputerState, ConnectedUser}
import services.state._
import services.{ComputerService, SSHOrderService}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@Singleton
class ComputerServiceImpl @Inject()(sSHOrderService: SSHOrderService, computerDAO: ComputerDAO)(implicit executionContext: ExecutionContext) extends ComputerService {

  private def executeSeveralWithStatus(ips: List[String])(fun: ((Computer,ComputerState, Seq[ConnectedUser]))=>ActionState) = {
    getSeveral(ips).map { computers =>
      val filtered: Seq[(Computer, ComputerState, Seq[ConnectedUser])] = computers.flatMap(computer => computer._2 match {
        case Some(statusWithConnectedUser) => Some((computer._1, statusWithConnectedUser._1, statusWithConnectedUser._2))
        case _ => None
      })
      val actionStates: Seq[ActionState] = filtered.map(fun)
      if (actionStates.exists(_ != ActionCompleted)) {
        Failed
      } else {
        ActionCompleted
      }
    }
  }

  private def executeSeveralSimple(ips: List[String])(fun: Computer=>ActionState) = {
    getSeveralSingle(ips).map { computers =>
      val actionStates = computers.map(fun)
      if (actionStates.exists(_ != ActionCompleted)) {
        Failed
      } else {
        ActionCompleted
      }
    }
  }

  override def add(computer: Computer): Future[ActionState] = {
    play.Logger.debug("Adding computer")
    computerDAO.add(computer)
  }

  /**
    * Edit a computer on the database. Updates its fields.
    *
    * @param computer Computer new data. Computer with the same ip address will be updated
    * @return State
    */
  override def edit(computer: Computer): Future[ActionState] = {
    play.Logger.debug("Editing computer")
    computerDAO.edit(computer)
  }

  /**
    * List all computers on the database
    *
    * @return Sequence of found computer
    */
  override def listAll: Future[Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]] = {

    computerDAO.listAll.map { computers =>
      val groupedComputers = computers
        // Grouping by the computer
        .groupBy(_._1)

      groupedComputers.map { groupedComputer =>
        val computer = groupedComputer._1
        val groupedComputerAndStatus = groupedComputer._2
        val cleanedStatus = groupedComputerAndStatus.map { computerStateWithUsers =>
          (computerStateWithUsers._2, computerStateWithUsers._3)
        }
        val groupedStatusWithUsers = cleanedStatus.groupBy(_._1)
        val cleanedStatusAndUsers = groupedStatusWithUsers.map { groupedState =>
          (groupedState._1, groupedState._2.flatMap(_._2))
        }
        val definedStatusAndUsers = cleanedStatusAndUsers.flatMap {
          case (Some(computerState), users) => Some((computerState, users))
          case _ => None
        }.toSeq
        val sortedStatusAndUsers = definedStatusAndUsers.sortBy(_._1.registeredDate.getTime).reverse
        (computer, sortedStatusAndUsers.headOption)
      }.toSeq.sortBy(_._1.ip)
    }
  }

  /**
    * List all computers on the database
    *
    * @return Sequence of all computers found
    */
  override def listAllSimple: Future[Seq[Computer]] = {
    computerDAO.listAllSimple.map(computers => computers.sortBy(_.ip))
  }

  /**
    * Get a computer by its IP
    *
    * @param ip IP of the computer to be looked for
    * @return Some computer with its states and connected users if found
    */
  override def get(ip: String): Future[Option[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]] = {
    play.Logger.debug(s"Looking for a single computer $ip")
    // Get computers with statuses from the database
    computerDAO.getWithStatus(ip).map { computers =>
      // Group the found computers with statuses by their computer
      computers.groupBy(_._1).map { computerWithStatus =>
        // Computer
        val computer = computerWithStatus._1
        // Clean the computer from here
        val statusesCleaned = computerWithStatus._2.map { computersWithStatusGrouped =>
          (computersWithStatusGrouped._2, computersWithStatusGrouped._3)
        }
        // Group status and connected users by their status
        val statusesGrouped = statusesCleaned.groupBy(_._1)
        // Clean the status from the second part of this tuple
        val statusesGroupedCleaned = statusesGrouped.map { status =>
          (status._1, status._2.flatMap(_._2))
        }.filter(_._1.isDefined).map { stateWithUsers =>
          (stateWithUsers._1.get, stateWithUsers._2)
        }.toSeq
        // Sort them by their date and get only its last state
        val statusesSorted = statusesGroupedCleaned.sortBy(_._1.registeredDate.getTime).reverse.headOption
        (computer, statusesSorted)
      }.toSeq.headOption
    }
  }

  /**
    * Get computers by their ips
    *
    * @param ips Ip list of the computers to be looked for
    * @return Sequence of found computers
    */
  override def getSeveral(ips: List[String]): Future[Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]] = {
    play.Logger.debug(s"Looking for several computers: $ips")
    // Get computers with statuses from the database
    computerDAO.getWithStatus(ips).map { computers =>
      // Group the found computers with statuses by their computer
      computers.groupBy(_._1).map { computerWithStatus =>
        // Computer
        val computer = computerWithStatus._1
        // Clean the computer from here
        val statusesCleaned = computerWithStatus._2.map { computersWithStatusGrouped =>
          (computersWithStatusGrouped._2, computersWithStatusGrouped._3)
        }
        // Group status and connected users by their status
        val statusesGrouped = statusesCleaned.groupBy(_._1)
        // Clean the status from the second part of this tuple
        val statusesGroupedCleaned = statusesGrouped.map { status =>
          (status._1, status._2.flatMap(_._2))
        }.filter(_._1.isDefined).map { stateWithUsers =>
          (stateWithUsers._1.get, stateWithUsers._2)
        }.toSeq
        // Sort them by their date and get only its last state
        val statusesSorted = statusesGroupedCleaned.sortBy(_._1.registeredDate.getTime).reverse.headOption
        (computer, statusesSorted)
      }.toSeq
    }
  }

  override def getSeveralSingle(ips: List[String]): Future[Seq[Computer]] = {
    computerDAO.get(ips)
  }

  override def installAPackage(ip: String, packages: String)(implicit username: String): Future[ActionState] = {
    val packagesSplitted = packages.split(", ").toList
    play.Logger.debug(s"Installing packages: ${packagesSplitted.mkString("[", ",", "]")}")
    get(ip).map {
      case Some((computer, _)) => sSHOrderService.installAPackage(computer, packagesSplitted)
      case _ => NotFound
    }
  }

  override def sendMessage(ip: List[String], message: String)(implicit username: String): Future[ActionState] = {
    executeSeveralWithStatus(ip){computer =>
      sSHOrderService.sendMessage(computer._1, message, computer._3)
    }
  }

  override def getSingle(ip: String): Future[Option[Computer]] = {
    computerDAO.get(ip)
  }

  override def delete(ip: String): Future[ActionState] = {
    computerDAO.delete(ip)
  }

  override def getWithStatus(ip: String): Future[Seq[(Computer, Option[ComputerState], Option[ConnectedUser])]] = {
    computerDAO.getWithStatus(ip)
  }

  override def shutdown(ips: List[String])(implicit username: String): Future[ActionState] = {
    executeSeveralSimple(ips)(sSHOrderService.shutdown)
  }

  override def upgrade(ips: List[String])(implicit username: String): Future[ActionState] =
    executeSeveralWithStatus(ips){computer =>
      sSHOrderService.upgrade(computer._1, computer._2)
  }

  override def unfreeze(ips: List[String])(implicit username: String): Future[ActionState] = {
    executeSeveralSimple(ips)(sSHOrderService.unfreeze)
  }

  override def sendCommand(ip: List[String], superUser: Boolean, command: String)(implicit username: String): Future[ActionState] = {
    executeSeveralSimple(ip){computer=>
      val (result, exitCode) = sSHOrderService.execute(computer, superUser, command)
      if(exitCode == 0) ActionCompleted else OrderFailed(result,exitCode)
    }
  }

  override def blockPage(ips: List[String], page: String)(implicit username: String): Future[ActionState] = {
    executeSeveralSimple(ips)(sSHOrderService.blockPage(_,page))
  }

  override def add(ip: String, name: Option[String], sSHUser: String, sSHPassword: String, description: Option[String], roomID: Option[Long]): Future[ActionState] = {
    val ips = ip.split(",")
    val names = name.getOrElse("").split(",")
    val futures = ips.zip(names).map { pair =>
      val newComputer = Computer(pair._1, Some(pair._2), sSHUser, sSHPassword, description, roomID)
      play.Logger.debug("Adding a new computer: " + newComputer)
      add(newComputer)
    }.toSeq
    Future.sequence(futures).map { states =>
      if (states.exists(_ != ActionCompleted)) {
        ActionCompleted
      } else {
        Failed
      }
    }

  }
}
