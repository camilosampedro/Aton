package services

import com.google.inject.ImplementedBy
import com.jcraft.jsch.JSchException
import model.{Computer, SSHOrder}
import services.impl.SSHOrderServiceImpl

/**
  * Created by camilo on 14/05/16.
  */
@ImplementedBy(classOf[SSHOrderServiceImpl])
trait SSHOrderService {
  def getMac(newComputer: Computer, username: String): Option[String]

  @throws(classOf[JSchException])
  def execute(computer: Computer, sshOrder: SSHOrder): (String, Int)
}
