import play.api.libs.json.{JsObject, Json, Reads, Writes}

/**
  * Created by camilosampedro on 1/01/17.
  */
package object model {
  implicit val laboratoryWrites: Writes[Laboratory] = Json.writes[Laboratory]
  implicit val computerStateWrites: Writes[ComputerState] = Json.writes[ComputerState]
  implicit val connectedUserWrites: Writes[ConnectedUser] = Json.writes[ConnectedUser]
  implicit val computerWrites: Writes[Computer] = new Writes[Computer] {
    override def writes(o: Computer): JsObject = Json.obj(
      "ip" -> o.ip,
      "description" -> o.description,
      "name" -> o.name,
      "roomID" -> o.roomID
    )
  }
  implicit val roomWrites: Writes[Room] = Json.writes[Room]

  implicit val computerStatePairWrites = new Writes[Option[(ComputerState, Seq[ConnectedUser])]] {
    def writes(state: Option[(ComputerState, Seq[ConnectedUser])]) = {
      state match {
        case Some(pair) =>
          Json.obj (
            "state" -> Json.toJson(pair._1),
            "users" -> pair._2.map(Json.toJson(_))
          )
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
    def writes(room: (Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])) = Json.obj (
      "room" -> Json.toJson(room._1),
      "computers" -> room._2.map(Json.toJson(_))
    )
  }

  implicit val listOfRoomsWrites = new Writes[Seq[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])]] {
    def writes(rooms: Seq[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])]) = Json.obj {
      "rooms" -> rooms.map(Json.toJson(_))
    }
  }

  implicit val laboratoryWithRoomsWrites = new Writes[(Laboratory, Seq[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])])] {
    def writes(laboratoryWithRooms: (Laboratory, Seq[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])])) = Json.obj (
      "laboratory" -> Json.toJson(laboratoryWithRooms._1),
      "rooms" -> Json.toJson(laboratoryWithRooms._2)
    )
  }
}
