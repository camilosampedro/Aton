package services.impl

import com.google.inject.{Inject, Singleton}
import dao.LaboratoryDAO
import model._
import services.LaboratoryService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilo on 21/05/16.
  */
@Singleton
class LaboratoryServiceImpl @Inject()(laboratoryDAO: LaboratoryDAO)(implicit executionContext: ExecutionContext) extends LaboratoryService {
  def get(id: Long ): Future[Option[(Laboratory, Map[Option[Room], Seq[(Computer, Option[(ComputerState,Seq[ConnectedUser])])]])]] ={
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
                .flatMap{
                  case (Some(computer),second)=>Some((computer,second.flatMap{
                    case (_,Some(state),user) => Some((state,user))
                    case _ => None
                  }))
                  case _ => None
                }
                .map { x =>
                  (x._1,x._2.groupBy(_._1).map{groupedState=>
                    (groupedState._1,groupedState._2.flatMap(_._2))
                  }.toSeq.sortBy(_._1.registeredDate.getTime).reverse.headOption)
                }.toSeq.sortBy(_._1.ip))
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
