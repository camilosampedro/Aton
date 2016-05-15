package services.impl

import com.google.inject.{Inject, Singleton}
import dao.ComputerDAO
import model.Computer
import play.Logger
import services.{ComputerService, SSHOrderService}
import services.exec.Execution

/**
  * Created by camilo on 14/05/16.
  */
@Singleton
class ComputerServiceImpl @Inject()(sSHOrderService: SSHOrderService, computerDAO: ComputerDAO) extends ComputerService {

  override def add(computer: Computer) = {
    computerDAO.add(completeMac(computer))
  }

  def completeMac(computer: Computer): Computer = {
    computer.copy(mac = sSHOrderService.getMac(computer))
  }
}
