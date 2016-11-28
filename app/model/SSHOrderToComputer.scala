package model

import java.sql.Timestamp

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
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
