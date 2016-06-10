package model.json

import model._
import play.api.libs.json.{Json, Writes}

/**
  * Created by camilosampedro on 20/05/16.
  */
object Writes {
  implicit val computerWrites = new Writes[Computer] {
    def writes(computer: Computer) = Json.obj {
      "ip" -> computer.ip
      "name" -> computer.name
      "description" -> computer.description
      "roomID" -> computer.roomID
    }
  }

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
    def writes(connectedUser: ConnectedUser) = Json.obj {
      "id" -> connectedUser.id
      "username" -> connectedUser.username
    }
  }

  implicit val computerStateWrites = new Writes[ComputerState] {
    def writes(state: ComputerState) = Json.obj{
      "mac"->state.mac
      "operatingSystem"->state.operatingSystem
      "state"->state.state
    }
  }

  implicit val computerStatePairWrites = new Writes[Option[(ComputerState, Seq[ConnectedUser])]] {
    def writes(state: Option[(ComputerState, Seq[ConnectedUser])]) = {
      state match {
        case Some(pair) => {
          Json.obj {
            "state" -> Json.toJson(pair._1)
            "users" -> pair._2.map(Json.toJson(_))
          }
        }
        case _ => Json.parse("")
      }
    }
  }

  implicit val computerWithStateWrites = new Writes[(Computer, Option[(ComputerState, Seq[ConnectedUser])])] {
    def writes(computerWithState: (Computer, Option[(ComputerState, Seq[ConnectedUser])])) = Json.obj (
      "state" -> Json.toJson(computerWithState._2),
      "computer" -> Json.toJson(computerWithState._1)
    )
  }

  implicit val roomsWrites = new Writes[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])] {
    def writes(room: (Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])) = Json.obj {
      "room" -> Json.toJson(room._1)
      "computers" -> room._2.map(Json.toJson(_))
    }
  }

  implicit val listOfRoomsWrites = new Writes[Seq[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])]] {
    def writes(rooms: Seq[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])]) = Json.obj {
      "rooms" -> rooms.map(Json.toJson(_))
    }
  }

  implicit val laboratoryWithRoomsWrites = new Writes[(model.Laboratory, Seq[(model.Room, Seq[(model.Computer, Option[(model.ComputerState, Seq[model.ConnectedUser])])])])] {
    def writes(laboratoryWithRooms: (model.Laboratory, Seq[(model.Room, Seq[(model.Computer, Option[(model.ComputerState, Seq[model.ConnectedUser])])])])) = Json.obj {
      "laboratory" -> Json.toJson(laboratoryWithRooms._1)
      "rooms" -> Json.toJson(laboratoryWithRooms._2)
    }
  }


}
