package model

import java.sql.Timestamp

/**
  * Created by camilo on 14/05/16.
  */
case class SSHOrderToComputer(
                               computerIp: String,
                               sshOrderId: Long,
                               sshOrderDatetime: Timestamp,
                               result: Option[String],
                               exitCode: Option[Int]) {
  def this(computerIp: String, sshOrderDatetime: Timestamp, sshOrderId: Long) =
    this(computerIp, sshOrderId, sshOrderDatetime, None, None)
}
