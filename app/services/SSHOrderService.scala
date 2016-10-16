package services

import com.google.inject.ImplementedBy
import com.jcraft.jsch.JSchException
import model._
import services.impl.SSHOrderServiceImpl
import services.state.ActionState

import scala.concurrent.Future

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[SSHOrderServiceImpl])
trait SSHOrderService {
  def listAll: Future[Seq[SSHOrder]]

  def get(id: Long): Future[Option[SSHOrder]]

  def delete(id: Long): Future[ActionState]

  def installAPackage(computer: Computer, programs: List[String]): ActionState

  def sendMessage(computer: Computer, message: String, users :Seq[ConnectedUser])(implicit username: String): ActionState

  def blockPage(computer: Computer, page: String)(implicit username: String): ActionState

  def execute(computer: Computer, superUser: Boolean, command: String)(implicit username:String): (String,Int)

  def whoAreUsing(computer: Computer)(implicit username: String): Seq[String]

  def checkState(computer: Computer)(implicit username: String): StateRef

  def check(computer: Computer)(implicit username: String): (ComputerState,Seq[ConnectedUser])

  def unfreeze(computer: Computer)(implicit username:String): ActionState

  def upgrade(computer: Computer,computerState: ComputerState)(implicit username:String): ActionState

  def shutdown(computer: Computer)(implicit username: String): ActionState

  def getMac(computer: Computer, operatingSystem: Option[String])(implicit username: String): Option[String]

  @throws(classOf[JSchException])
  def execute(computer: Computer, sshOrder: SSHOrder): (String, Int)

  def getOperatingSystem(computer: Computer)(implicit username: String): Option[String]
}
