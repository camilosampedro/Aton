package services.exec

import com.jcraft.jsch.JSchException
import fr.janalyse.ssh._
import model.Computer
import services.exec.SSHFunction._

/**
  * Created by camilosampedro on 12/05/16.
  */
object Execution {

  @throws(classOf[JSchException])
  def execute(computer: Computer, command: String): (String, Int) = {
    play.Logger.debug(s"""Executing: "$command" into: $computer""")
    val settings = SSHOptions(host = computer.ip, username = computer.SSHUser, password = computer.SSHPassword)
    jassh.SSH.once(settings)(_.executeWithStatus(command))
  }

  @throws(classOf[JSchException])
  def execute(computer: Computer, command: String, sudo: Boolean): (String, Int) = {
    val settings = SSHOptions(host = computer.ip, username = computer.SSHUser, password = computer.SSHPassword)
    jassh.SSH.shell(settings) { ssh =>
      //ssh.executeWithExpects("sudo -S su",List(new Expect(_.contains("password"),settings.password.password.getOrElse(""))))
      //ssh.become("root",settings.password.password)
      ssh.executeWithExpects("""SUDO_PROMPT="prompt" sudo -S su -""", List(new Expect(_.endsWith("prompt"), "Hu8co.d: !")))
      val (result, exitCode) = ssh.executeWithStatus(command)
      ssh.execute("exit")
      return (result, exitCode)
    }
  }

  @throws(classOf[JSchException])
  def getMac(computer: Computer): Option[String] = {
    play.Logger.debug(s"""Looking for mac of "${computer.ip}"""")
    for (order <- macOrders) {
      play.Logger.debug(s"""Trying "${order}"""")
      val (result, _) = execute(computer, order)
      play.Logger.debug(s"""Result: $result""")
      if (result != "") return Some(result)
    }
    return None
  }
}
