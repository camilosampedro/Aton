package model.json

import model._
import play.api.libs.json.{Json, Writes}

/**
  * Created by camilosampedro on 20/05/16.
  */
object Writes {
  implicit val laboratoryWrites = new Writes[Laboratory] {
    def writes(laboratory: Laboratory) = Json.obj {
      "id" -> laboratory.id
      "name" -> laboratory.name
      "location" -> laboratory.location
      "administration" -> laboratory.administration
    }
  }

  implicit val roomWrites = new Writes[Room] {
    def writes(room: Room) = Json.obj {
      "id" -> room.id
      "name" -> room.name
      "audiovisualResources" -> room.audiovisualResources
      "basicTools" -> room.basicTools
      "laboratoryId" -> room.laboratoryID
    }
  }

  implicit val connectedUserWrites = new Writes[ConnectedUser] {
    def writes(connectedUser: ConnectedUser) = Json.obj{
      "id" -> connectedUser.id
      "username" -> connectedUser.username
    }
  }

  implicit val roomsWrites = new Writes[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])] {
    def writes(room: (Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])) = Json.obj {
      "room" -> Json.toJson(room._1)
      "computers" -> Json.toJson(room._2)
    }
  }

  implicit val listOfRoomsWrites = new Writes[Seq[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])]]{
    def writes(rooms:Seq[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])]) = Json.obj{
      "rooms" -> rooms.map(Json.toJson(_))
    }
  }

  implicit val computerWithStateWrites = new Writes[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]{
    def writes(computerWithState:(Computer, Option[(ComputerState, Seq[ConnectedUser])])) = Json.obj{
      "computer" -> Json.toJson(computerWithState._1)
      "state"-> Json.toJson(computerWithState._2)
    }
  }
}
