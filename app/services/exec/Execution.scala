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

  def execute(computer: Computer, command: String, sudo: Boolean) = {
    val settings = SSHOptions(host = computer.ip, username = computer.SSHUser, password = computer.SSHPassword)
    jassh.SSH.shell(settings) { ssh =>
      ssh.executeWithExpects("sudo -S su",List(new Expect(_.contains("password"),settings.password.password.getOrElse(""))))
      //ssh.become("root",settings.password.password)
      ssh.executeWithExpects("""SUDO_PROMPT="prompt" sudo -S su -""",List(new Expect(_.endsWith("prompt"),"Hu8co.d: !")))
      ssh.executeWithStatus(command)
    }
  }

}
