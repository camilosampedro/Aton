package model

/**
  * POJO with the basic Laboratory information (Used by the Laboratory DAO, Service and Controller)
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class Laboratory(
                       id: Long,
                       name: String,
                       location: Option[String],
                       administration: Option[String])