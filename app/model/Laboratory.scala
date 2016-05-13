package model

/**
  * POJO with the basic Laboratory information (Used by the Laboratory DAO, Service and Controller)
  */
case class Laboratory(
                       id: Long,
                       name: String,
                       location: String,
                       administration: String
                     )
