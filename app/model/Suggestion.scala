package model

/**
  * POJO with the basic Suggestion information (Used by the Suggestion DAO, Service and Controller)
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class Suggestion(
                       id: Long,
                       suggestionText: String,
                       registeredDate: java.sql.Timestamp,
                       username: Option[String])
