package model.json

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class ComputerJson(
                             ip: String,
                             name: Option[String],
                             SSHUser: String,
                             SSHPassword: String,
                             description: Option[String],
                             roomID: Option[Long]
                           )
