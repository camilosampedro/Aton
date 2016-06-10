package model

import play.api.libs.json.{Json, Writes}

/**
  * POJO with the basic Computer information (Used by the Computer DAO, Service and Controller)
  */
case class Computer(
                     ip: String,
                     name: Option[String],
                     SSHUser: String,
                     SSHPassword: String,
                     description: Option[String],
                     roomID: Option[Long]
                   ){

}
