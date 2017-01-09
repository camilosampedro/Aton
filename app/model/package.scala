import play.api.libs.json._

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
  implicit val roomReads: Reads[Room] = Json.reads[Room]

  implicit val computerStatePairWrites = new Writes[Option[(ComputerState, Seq[ConnectedUser])]] {
    def writes(state: Option[(ComputerState, Seq[ConnectedUser])]): JsValue = {
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
    def writes(computerWithState: (Computer, Option[(ComputerState, Seq[ConnectedUser])])): JsObject = Json.obj (
      "state" -> Json.toJson(computerWithState._2),
      "computer" -> Json.toJson(computerWithState._1)
    )
  }

  implicit val roomsWrites = new Writes[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])] {
    def writes(room: (Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])): JsObject = Json.obj (
      "room" -> Json.toJson(room._1),
      "computers" -> room._2.map(Json.toJson(_))
    )
  }

  implicit val laboratoryWithRoomsWrites = new Writes[(Laboratory, Seq[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])])] {
    def writes(laboratoryWithRooms: (Laboratory, Seq[(Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])])])): JsObject = Json.obj (
      "laboratory" -> Json.toJson(laboratoryWithRooms._1),
      "rooms" -> Json.toJson(laboratoryWithRooms._2.map(Json.toJson(_)))
    )
  }

  implicit val laboratoryWithChildrenWrites = new Writes[(Laboratory, Map[Room,Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]])]{
    override def writes(o: (Laboratory, Map[Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]])) = {
      val roomsWithComputers = o._2
      val laboratoryObject = o._1
      val roomsConverted = roomsWithComputers.toSeq
      val grouped = roomsConverted.groupBy(_._1)
      val resultRooms = grouped.map(filtered=>(filtered._1,filtered._2.map(_._2).head)).toSeq
      Json.toJson((laboratoryObject,resultRooms))
    }
  }
}
