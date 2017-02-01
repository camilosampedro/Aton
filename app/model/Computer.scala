package model

/**
  * POJO with the basic Computer information (Used by the Computer DAO, Service and Controller)
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class Computer(
                     ip: String,
                     name: Option[String],
                     SSHUser: String,
                     SSHPassword: String,
                     description: Option[String],
                     roomID: Option[Long])