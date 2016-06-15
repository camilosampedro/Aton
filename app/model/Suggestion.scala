package model

/**
  * POJO with the basic Suggestion information (Used by the Suggestion DAO, Service and Controller)
  */
case class Suggestion(
                       id: Long,
                       suggestionText: String,
                       registeredDate: java.sql.Timestamp,
                       username: Option[String]
                     )
