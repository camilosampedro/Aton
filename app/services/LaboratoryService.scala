package services

import com.google.inject.ImplementedBy
import model.{Computer, ComputerState, Laboratory, Room}
import services.impl.LaboratoryServiceImpl

import scala.concurrent.Future

/**
  * Created by camilo on 21/05/16.
  */
@ImplementedBy(classOf[LaboratoryServiceImpl])
trait LaboratoryService {
  def get(id: Long): Future[Option[(Laboratory, Map[Option[Room], Seq[(Computer, Option[ComputerState])]])]]
}
