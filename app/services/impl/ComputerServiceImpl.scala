package services.impl

import com.google.inject.{Inject, Singleton}
import dao.ComputerDAO
import model.{Computer, ComputerState, ConnectedUser}
import services.{ComputerService, SSHOrderService}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@Singleton
class ComputerServiceImpl @Inject()(sSHOrderService: SSHOrderService, computerDAO: ComputerDAO)(implicit executionContext: ExecutionContext) extends ComputerService {



  override def add(computer: Computer)(implicit executionContext: ExecutionContext): Future[String] = {
    play.Logger.debug("Adding computer")
    computerDAO.add(computer)
  }

  override def edit(computer: Computer): Future[Int] = {
    play.Logger.debug("Editing computer")
    computerDAO.edit(computer)
  }

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

  override def listAllSimple: Future[Seq[Computer]] = {
    computerDAO.listAllSimple.map(computers => computers.sortBy(_.ip))
  }

  override def get(ip: String): Future[Option[(Computer, Option[(ComputerState,Seq[ConnectedUser])])]] = computerDAO.getWithStatus(ip).map{computers=>
    computers.groupBy(_._1).map{ computerWithStatus=>
      (computerWithStatus._1,computerWithStatus._2.map(x=>(x._2,x._3)).groupBy(_._1).map{status=>
        (status._1,status._2.flatMap(_._2))
      }.filter(_._1.isDefined).map(x=>(x._1.get,x._2)).toSeq.sortBy(_._1.registeredDate.getTime).reverse.headOption)
    }.toSeq.map{triple=>
      (triple._1,triple._2)
    }.headOption
  }

  override def get(severalComputers: List[String]): Future[Seq[Computer]] = {
    computerDAO.get(severalComputers)
  }
}
