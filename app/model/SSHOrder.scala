package model

import java.sql.Timestamp

/**
  * POJO with the basic SSH Order information (Used by the SSH Order DAO, Service and Controller)
  */
case class SSHOrder(
                     sentDatetime: Timestamp,
                     superUser: Boolean,
                     interrupt: Boolean,
                     command: String,
                     webUser: String
                   ) {
  def this(sentDatetime: Timestamp,
           superUser: Boolean,
           interrupt: Boolean,
           command: String) = this(sentDatetime, superUser, interrupt, command, "")

  def this(sentDatetime: Timestamp, superUser: Boolean, command: String) = this(sentDatetime, superUser, false, command, "")

  def this(sentDatetime: Timestamp, command: String) = this(sentDatetime, false, false, command, "")
}
