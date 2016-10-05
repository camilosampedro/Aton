package model

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class User(
                 username: String,
                 password: String,
                 name: Option[String],
                 role: Int)
