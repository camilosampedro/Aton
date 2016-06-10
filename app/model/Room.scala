package model

import play.api.libs.json.{Json, Writes}

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
