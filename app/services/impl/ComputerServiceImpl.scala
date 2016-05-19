package services.impl

import com.google.inject.{Inject, Singleton}
import dao.ComputerDAO
import model.Computer
import services.{ComputerService, SSHOrderService}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by camilo on 14/05/16.
  */
@Singleton
class ComputerServiceImpl @Inject()(sSHOrderService: SSHOrderService, computerDAO: ComputerDAO) extends ComputerService {

  override def add(computer: Computer)(implicit username: String): Future[String] = {
    play.Logger.debug("Adding computer")
    computerDAO.add(computer)
  }
}
