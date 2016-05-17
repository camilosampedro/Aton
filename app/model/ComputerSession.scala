package model

/**
  * POJO with the basic ComputerSession information (Used by the ComputerSession DAO, Service and Controller)
  */
case class ComputerSession(
                            computerIp: String,
                            connectionTime: java.sql.Timestamp,
                            connectedUser: Option[String],
                            active: Option[Boolean]
                          )
