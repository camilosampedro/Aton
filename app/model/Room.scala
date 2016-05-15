package model

/**
  * POJO with the basic Room information (Used by the Room DAO, Service and Controller)
  */
case class Room(
                 id: Long,
                 name: String,
                 audiovisualResources: Option[String],
                 basicTools: Option[String],
                 laboratoryID: Long
               )
