package model.json

import model.{Computer, ComputerState, Laboratory, Room}
import play.api.libs.json.{Json, Writes}

/**
  * Created by camilosampedro on 20/05/16.
  */
object Writes {
  implicit val laboratoryWrites = new Writes[Laboratory] {
    def writes(laboratory: Laboratory) = Json.obj(
      "id" -> laboratory.id,
      "name" -> laboratory.name,
      "administration" -> laboratory.administration,
      "location" -> laboratory.location
    )
  }

  implicit val laboratoryChildrenWrites = new Writes[Map[Option[Room], Seq[(Computer, ComputerState)]]] {
    def writes(map: Map[Option[Room], Seq[(Computer, ComputerState)]]) = {
      Json.obj()
    }
  }
}
