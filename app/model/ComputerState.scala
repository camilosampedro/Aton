package model

/**
  * POJO with the basic Computer State information (Used by the ComputerState DAO, Service and Controller)
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class ComputerState(
                          computerIp: String,
                          registeredDate: java.sql.Timestamp,
                          state: Int,
                          operatingSystem: Option[String],
                          mac: Option[String])
