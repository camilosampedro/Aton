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

  override def add(computer: Computer, username: String): Future[Int] = {
    play.Logger.debug("Adding computer")
    Await.result(computerDAO.add(computer),Duration.Inf)
    play.Logger.debug("Computer added... Looking for mac")
    computerDAO.edit(completeMac(computer, username))
  }

  def completeMac(computer: Computer, username: String): Computer = {
    computer.copy(mac = sSHOrderService.getMac(computer, username))
  }
}
