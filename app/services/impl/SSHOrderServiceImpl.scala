package services.impl

import java.sql.Timestamp
import java.util.Calendar

import com.google.inject.{Inject, Singleton}
import com.jcraft.jsch.JSchException
import dao.{SSHOrderDAO, SSHOrderToComputerDAO}
import fr.janalyse.ssh.{Expect, SSHOptions}
import model.{Computer, SSHOrder, SSHOrderToComputer}
import services.SSHOrderService
import services.exec.SSHFunction._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by camilo on 14/05/16.
  */
@Singleton
class SSHOrderServiceImpl @Inject()(sSHOrderDAO: SSHOrderDAO, sSHOrderToComputerDAO: SSHOrderToComputerDAO) extends SSHOrderService{
  @throws(classOf[JSchException])
  override def execute(computer: Computer, sshOrder: SSHOrder): (String, Int) =  {
    play.Logger.debug(s"""Executing: ${sshOrder} into: $computer""")
    sSHOrderDAO.add(sshOrder).map{x=>play.Logger.debug(x)}
    val settings = SSHOptions(host = computer.ip, username = computer.SSHUser, password = computer.SSHPassword)
    val(result,exitCode) = if (sshOrder.superUser) {
       jassh.SSH.shell(settings) { ssh =>
        //ssh.executeWithExpects("sudo -S su",List(new Expect(_.contains("password"),settings.password.password.getOrElse(""))))
        //ssh.become("root",settings.password.password)
        ssh.executeWithExpects("""SUDO_PROMPT="prompt" sudo -S su -""", List(new Expect(_.endsWith("prompt"), "Hu8co.d: !")))
        val (result, exitCode) = ssh.executeWithStatus(sshOrder.command)
        ssh.execute("exit")
        (result, exitCode)
      }
    } else {
      jassh.SSH.once(settings)(_.executeWithStatus(sshOrder.command))
    }
    val resultSSHOrder = SSHOrderToComputer(computer.ip,sshOrder.sentDatetime,Some(result),Some(exitCode))
    sSHOrderToComputerDAO.update(resultSSHOrder).map{x=>play.Logger.debug(x.toString)}
    (result,exitCode)
  }

  @throws(classOf[JSchException])
  override def getMac(computer: Computer): Option[String] = {
    play.Logger.debug(s"""Looking for mac of "${computer.ip}"""")
    for (order <- macOrders) {
      play.Logger.debug(s"""Trying "${order}"""")
      val (result, _) = execute(computer, new SSHOrder(new Timestamp(Calendar.getInstance().getTime.getTime),order))
      play.Logger.debug(s"""Result: $result""")
      if (result != "") return Some(result)
    }
    return None
  }
}
