package services

import com.google.inject.ImplementedBy
import model.Room
import services.impl.{ComputerServiceImpl, RoomServiceImpl}
import services.state.ActionState

import scala.concurrent.Future

/**
  * Created by camilo on 13/10/16.
  */
@ImplementedBy(classOf[RoomServiceImpl])
trait RoomService {
  def add(room: Room): Future[ActionState]
  def delete(id: Long): Future[ActionState]

  def listAll: Future[Seq[Room]]

  def get(id: Long): Future[Option[Room]]
  def getByLaboratory(id: Long): Future[Seq[Room]]
}
