package services.impl

import java.sql.Timestamp
import java.util.Calendar

import com.google.inject.{Inject, Singleton}
import com.jcraft.jsch.{JSch, JSchException}
import dao.{SSHOrderDAO, SSHOrderToComputerDAO}
import fr.janalyse.ssh.{Expect, SSHOptions}
import model._
import services.SSHOrderService
import services.exec.SSHFunction._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by camilo on 14/05/16.
  */
@Singleton
class SSHOrderServiceImpl @Inject()(sSHOrderDAO: SSHOrderDAO, sSHOrderToComputerDAO: SSHOrderToComputerDAO) extends SSHOrderService {



  @throws(classOf[JSchException])
  override def execute(computer: Computer, sshOrder: SSHOrder): (String, Int) = {
    play.Logger.debug(s"""Executing: $sshOrder into: $computer""")
    val future = sSHOrderDAO.add(sshOrder).map {
      case Some(id) =>
        val settings = generateSSHSettings(computer, sshOrder)
        val (result, exitCode) = if (sshOrder.superUser) {
          executeWithSudoWorkaround(sshOrder,settings)
          jassh.SSH.shell(settings) { ssh =>
            /*ssh.executeWithExpects("sudo -S su", List(new Expect(_.contains("password"), settings.password.password.getOrElse(""))))
            ssh.become("root", settings.password.password)*/

            ssh.executeWithExpects("""SUDO_PROMPT="prompt" sudo -S su -""", List(new Expect(_.endsWith("prompt"), settings.password.password.getOrElse(""))))
            val (result, exitCode) = ssh.executeWithStatus(sshOrder.command)
            ssh.execute("exit")
            (result, exitCode)
          }
          //jassh.SSH.once(settings)(_.executeWithStatus("sudo " + sshOrder.command))
        } else {
          jassh.SSH.once(settings)(_.executeWithStatus(sshOrder.command))
        }
        //play.Logger.debug("ID: " + id)
        val resultSSHOrder = SSHOrderToComputer(computer.ip, id, now, Some(result), Some(exitCode))
        sSHOrderToComputerDAO.add(resultSSHOrder)
        (result, exitCode)
      case _ =>
        ("", 0)
    }

    Await.result(future, Duration.Inf)
  }

  override def getMac(computer: Computer, operatingSystem: Option[String])(implicit username: String): Option[String] = {
    //play.Logger.debug(s"""Looking for mac of "${computer.ip}"""")
    val orders = operatingSystem match {
      case Some(os)=> macOrders(os)
      case _ => macOrders("")
    }
    for (order <- orders) {
      //play.Logger.debug(s"""Trying "${order}"""")
      try {
        val (result, _) = execute(computer, new SSHOrder(now, order, username))
        //play.Logger.debug(s"""Result: $result""")
        if (result != "") Some(result)
      } catch {
        case e: JSchException=>None
        case e: Exception =>
          play.Logger.error("An error occurred while looking for computer's mac: " + computer, e)
          None
      }
    }
    None
  }

  @throws[JSchException]
  override def shutdown(computer: Computer)(implicit username: String): Boolean = {
    val (_, _) = execute(computer, new SSHOrder(now, superUser = false, interrupt = true, command = shutdownOrder, username = username))
    true
  }

  private def now = new Timestamp(Calendar.getInstance().getTime.getTime)

  @throws[JSchException]
  override def upgrade(computer: Computer)(implicit username: String): (String, Boolean) = {
    val (result, exitCode) = execute(computer, new SSHOrder(now, superUser = false, interrupt = true, command = upgradeOrder, username = username))
    if (exitCode == 0) {
      ("", true)
    } else {
      (result, false)
    }
  }

  @throws[JSchException]
  override def unfreeze(computer: Computer)(implicit username: String): (String, Boolean) = ???

  @throws[JSchException]
  override def getOperatingSystem(computer: Computer)(implicit username: String) = {
    try {
      val (result, exitCode) = execute(computer, new SSHOrder(now, superUser = false, interrupt = true, command = operatingSystemCheck, username = username))
      if (exitCode == 0) Some(result) else None
    }
    catch {
      case e: Exception => None
    }
  }


  override def check(computer: Computer)(implicit username: String): (ComputerState, Seq[ConnectedUser]) = {
    //play.Logger.debug(s"""Checking the $computer's state""")
    //play.Logger.debug(s"""Checking if $computer's on""")
    try{
      val state = checkState(computer)
      play.Logger.debug(s"""$computer is  $state""")
      val date = now
      val (operatingSystem, mac, whoIsUsing) = if(state != Connected()){
        (None,None,Seq.empty)
      } else {
        val os = getOperatingSystem(computer)
        (os,getMac(computer,os), whoAreUsing(computer).map { username => ConnectedUser(0, username, computer.ip, date)})
      }
      (ComputerState(computer.ip, date, state.id, operatingSystem, mac), whoIsUsing)
    } catch {
      case e:Exception=> play.Logger.error(s"There was an error checking $computer's state")
        (ComputerState(computer.ip,now,NotConnected().id,None,None),Seq.empty)

    }

  }

  override def checkState(computer: Computer)(implicit username: String): StateRef = {
    val sSHOrder = new SSHOrder(now, false, false, dummy, username)
    val settings = generateSSHSettings(computer, sSHOrder)
    try {
      val isConnected = jassh.SSH.once(settings)(_.executeWithStatus(sSHOrder.command)._1 == "Ping from Aton")
      if(isConnected){
        Connected()
      } else {
        NotConnected()
      }
    } catch {
      case ex: JSchException =>
        ex.getMessage match {
          case "Auth fail" => AuthFailed()
          case "timeout: socket is not established" => NotConnected()
          case e =>play.Logger.error(s"The error checking $computer was : " + e)
            UnknownError()
        }
      case e: Exception => play.Logger.error("There was an error checking for " + computer + "'s state",e)
        UnknownError()
    }
  }


  private def generateSSHSettings(computer: Computer, sSHOrder: SSHOrder) = SSHOptions(host = computer.ip, username = computer.SSHUser, password = computer.SSHPassword, connectTimeout = 1000,prompt = Some("prompt"))

  @throws[JSchException]
  override def whoAreUsing(computer: Computer)(implicit username: String): Seq[String] = {
    try {
      val (result, _) = execute(computer, new SSHOrder(now, false, false, userListOrder, username))
      for (user <- result.split("\n") if user != "") yield user
    } catch {
      case e: Exception => Seq()
    }
  }

  private def executeWithSudoWorkaround(sshOrder: SSHOrder, settings: SSHOptions) = {
    val jsch = new JSch()
    val sshSession = jsch.getSession(settings.username,settings.host,settings.port)
    sshSession.setConfig("StrictHostKeyChecking", "no")
    settings.password.password match {
      case Some(password) =>sshSession.setPassword(password)
        sshSession.connect()
      case _ =>
    }

  }
}
