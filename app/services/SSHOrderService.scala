package services

import com.google.inject.ImplementedBy
import com.jcraft.jsch.JSchException
import model._
import services.impl.SSHOrderServiceImpl

/**
  * Created by camilo on 14/05/16.
  */
@ImplementedBy(classOf[SSHOrderServiceImpl])
trait SSHOrderService {

  def whoAreUsing(computer: Computer)(implicit username: String): Seq[String]

  def checkState(computer: Computer)(implicit username: String): StateRef

  def check(computer: Computer)(implicit username: String): (ComputerState,Seq[ConnectedUser])

  def unfreeze(computer: Computer)(implicit username:String): (String,Boolean)

  def upgrade(computer: Computer)(implicit username:String): (String,Boolean)

  def shutdown(computer: Computer)(implicit username: String): Boolean

  def getMac(computer: Computer, operatingSystem: Option[String])(implicit username: String): Option[String]

  @throws(classOf[JSchException])
  def execute(computer: Computer, sshOrder: SSHOrder): (String, Int)

  def getOperatingSystem(computer: Computer)(implicit username: String): Option[String]
}
