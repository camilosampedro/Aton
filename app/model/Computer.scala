package model

/**
  * POJO with the basic Computer information (Used by the Computer DAO, Service and Controller)
  */
case class Computer(
                     ip: String,
                     name: String,
                     mac: String,
                     SSHUser: String,
                     SSHPassword: String,
                     description: String,
                     roomID: Option[Long]
                   )
