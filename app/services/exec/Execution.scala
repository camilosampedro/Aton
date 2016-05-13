package services.exec

import fr.janalyse.ssh._
import model.Computer

/**
  * Created by camilosampedro on 12/05/16.
  */
object Execution {

  def execute(computer: Computer, command: String): (String, Int) = {
    val settings = SSHOptions(host = computer.ip, username = computer.SSHUser, password = computer.SSHPassword)
    jassh.SSH.once(settings)(_.executeWithStatus(command))
  }

}
