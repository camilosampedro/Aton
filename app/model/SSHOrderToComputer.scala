package model

import java.sql.Timestamp

/**
  * Created by camilo on 14/05/16.
  */
case class SSHOrderToComputer(
                               computerIp: String,
                               sshOrderId: Timestamp,
                               result: Option[String],
                               exitCode: Option[Int]) {
  def this(computerIp: String, sshOrderId: Timestamp) = this(computerIp, sshOrderId, None, None)
}
