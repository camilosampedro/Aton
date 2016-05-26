package services.impl

import com.google.inject.{Inject, Singleton}
import dao.ComputerDAO
import model.{Computer, ComputerState, ConnectedUser}
import services.{ComputerService, SSHOrderService}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 14/05/16.
  */
@Singleton
class ComputerServiceImpl @Inject()(sSHOrderService: SSHOrderService, computerDAO: ComputerDAO)(implicit executionContext: ExecutionContext) extends ComputerService {

  override def add(computer: Computer): Future[String] = {
    play.Logger.debug("Adding computer")
    computerDAO.add(computer)
  }

  override def edit(computer: Computer): Future[Int] = {
    play.Logger.debug("Editing computer")
    computerDAO.edit(computer)
  }

  override def listAll: Future[Seq[(Computer, Option[ComputerState], Seq[ConnectedUser])]] = {

    computerDAO.listAll.map(computers =>

      computers
        // Grouping the computers
        .groupBy(_._1)
        .map{computer=>
        (computer._1,computer._2.flatMap(_._2).sortBy(_.registeredDate.getTime).headOption)
      }.toSeq
    )
  }
}
