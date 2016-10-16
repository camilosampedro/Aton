package services.impl

import com.google.inject.{Inject, Singleton}
import dao.RoomDAO
import model.Room
import services.RoomService
import services.state.ActionState

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 13/10/16.
  */
@Singleton
class RoomServiceImpl @Inject()(roomDAO: RoomDAO)(implicit executionContext: ExecutionContext) extends RoomService {
  override def listAll: Future[Seq[Room]] = roomDAO.listAll

  override def get(id: Long): Future[Option[Room]] = roomDAO.get(id)

  override def getByLaboratory(id: Long): Future[Seq[Room]] = roomDAO.getByLaboratory(id)

  override def add(room: Room): Future[ActionState] = roomDAO.add(room)

  override def delete(id: Long): Future[ActionState] = roomDAO.delete(id)
}
