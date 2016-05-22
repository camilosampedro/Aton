package services.impl

import com.google.inject.{Inject, Singleton}
import dao.ComputerDAO
import model.{Computer, ComputerState}
import services.{ComputerService, SSHOrderService}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by camilo on 14/05/16.
  */
@Singleton
class ComputerServiceImpl @Inject()(sSHOrderService: SSHOrderService, computerDAO: ComputerDAO) extends ComputerService {

  override def add(computer: Computer): Future[String] = {
    play.Logger.debug("Adding computer")
    computerDAO.add(computer)
  }

  override def edit(computer: Computer): Future[Int] = {
    play.Logger.debug("Editing computer")
    computerDAO.edit(computer)
  }

  override def listAll: Future[Seq[(Computer, Option[ComputerState])]] = computerDAO.listAll
}
