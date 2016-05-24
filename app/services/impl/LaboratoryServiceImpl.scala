package services.impl

import com.google.inject.{Inject, Singleton}
import dao.LaboratoryDAO
import model.{Computer, ComputerState, Laboratory, Room}
import services.LaboratoryService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 21/05/16.
  */
@Singleton
class LaboratoryServiceImpl @Inject()(laboratoryDAO: LaboratoryDAO)(implicit executionContext: ExecutionContext) extends LaboratoryService {
  def get(id: Long ): Future[Option[(Laboratory, Map[Option[Room], Seq[(Computer, Option[ComputerState])]])]] ={
    laboratoryDAO.getWithChildren(id).map { res =>
      val grouped = res.groupBy(_._1)
      grouped.headOption match {
        case Some((laboratory, rooms)) =>
          val roomsWithComputers = rooms.map {
            row => (row._2, row._3)
          }.groupBy {
            row => row._1
          }.map {
            case (k, v) =>
              (k, v
                .map(_._2)
                .groupBy(_._1)
                .filter(_._1.isDefined)
                .map(x => (x._1.get, x._2.map(_._2)))
                .map { x =>
                  (x._1,x._2.flatten.sortBy(_.registeredDate.getTime).headOption)
                }.toSeq)
          }.filter {
            row => row._1.isDefined
          }
          Some(laboratory,roomsWithComputers)
        case e =>
          None
      }
    }
  }

  override def listAll: Future[Seq[Laboratory]] = laboratoryDAO.listAll
}
