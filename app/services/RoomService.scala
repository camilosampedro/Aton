package services

import com.google.inject.ImplementedBy
import model.Room
import services.impl.{ComputerServiceImpl, RoomServiceImpl}

import scala.concurrent.Future

/**
  * Created by camilo on 13/10/16.
  */
@ImplementedBy(classOf[RoomServiceImpl])
trait RoomService {
  def listAll: Future[Seq[Room]]

  def get(id: Long): Future[Option[Room]]
  def getByLaboratory(id: Long): Future[Seq[Room]]
}
