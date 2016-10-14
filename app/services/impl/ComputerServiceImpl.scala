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

  override def add(computer: Computer): Future[ActionState] = {
    play.Logger.debug("Adding computer")
    computerDAO.add(computer)
  }

  /**
    * Edit a computer on the database. Updates its fields.
    * @param computer Computer new data. Computer with the same ip address will be updated
    * @return State
    */
  override def edit(computer: Computer): Future[ActionState] = {
    play.Logger.debug("Editing computer")
    computerDAO.edit(computer)
  }

  /**
    * List all computers on the database
    * @return Sequence of found computer
    */
  override def listAll: Future[Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]] = {

    computerDAO.listAll.map(computers =>

      computers
        // Grouping by the computer
        .groupBy(_._1)

        .map { groupedComputer =>
          (groupedComputer._1, groupedComputer._2.map { computerStateWithUsers =>
            (computerStateWithUsers._2, computerStateWithUsers._3)
          }.groupBy(_._1).map { groupedState =>
            (groupedState._1, groupedState._2.flatMap(_._2))
          }.flatMap {
            case (Some(computerState), users) => Some((computerState, users))
            case _ => None
          }.toSeq.sortBy(_._1.registeredDate.getTime).headOption)
        }.toSeq.sortBy(_._1.ip)
    )
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
  override def getSeveral(ips: List[String]): Future[Seq[Computer]] = {
    play.Logger.debug(s"Looking for several computers: $ips")
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

  override def sendMessage(ip: String, message: String)(implicit username: String): Future[ActionState] = {
    get(ip).map {
      case Some((computer, Some((_, connectedUsers)))) =>
        sSHOrderService.sendMessage(computer, message, connectedUsers)
      case Some((computer, _)) => Empty
      case _ => NotFound
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

  override def shutdown(ip: String)(implicit username: String): Future[ActionState] = {
    getSingle(ip).map {
      case Some(computer) => sSHOrderService.shutdown(computer)
      case _ => Failed
    }
  }

  override def shutdown(ips: List[String])(implicit username: String): Future[ActionState] = {
    getSeveral(ips).map{computers=>
      val actionStates = computers.map(sSHOrderService.shutdown(_))
      if(actionStates.exists(_!=Completed)){
        Failed
      } else {
        Completed
      }
    }
  }

  override def upgrade(ip: String)(implicit username: String): Future[ActionState] = {
    get(ip).map{
      case Some((computer, Some((computerState,_)))) => sSHOrderService.upgrade(computer, computerState)
      case Some((computer, None)) => NotChecked
      case _ => NotFound
    }
  }

  override def unfreeze(ip: String)(implicit username: String): Future[ActionState] = {
    getSingle(ip).map{
      case Some(computer) => sSHOrderService.unfreeze(computer)
      case _ => NotFound
    }
  }

  override def sendCommand(ip: String, superUser: Boolean, command: String)(implicit username: String): Future[ActionState] = {
    getSingle(ip).map{
      case Some(computer) if sSHOrderService.execute(computer,superUser,command)._2 == 0 => Completed
      case Some(computer) => Failed
      case _ => NotFound
    }
  }

  override def blockPage(ip: String, page: String)(implicit username: String): Future[ActionState] = {
    getSingle(ip).map{
      case Some(computer) => sSHOrderService.blockPage(computer,page)
      case _ => NotFound
    }
  }
}
