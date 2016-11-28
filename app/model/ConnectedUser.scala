package model

/**
  * POJO with the basic ComputerSession information (Used by the ComputerSession DAO, Service and Controller)
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class ConnectedUser(
                          id: Int,
                          username: String,
                          computerStateComputerIp: String,
                          computerStateRegisteredDate: java.sql.Timestamp)
