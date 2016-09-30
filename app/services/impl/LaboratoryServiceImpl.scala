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
  /**
    * Get a laboratory by its ID
    *
    * @param id Laboratory ID
    * @return Laboratory with rooms and each room with computers
    */
  def get(id: Long): Future[Option[(Laboratory, Map[Option[Room], Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]])]] = {
    // Access to database using the laboratory's DAO
    laboratoryDAO.getWithChildren(id).map { res =>
      // res will have a sequence of (laboratory, room, computer, computerState, connectedUser)
      // Then group it by its first element: Laboratory
      val grouped = res.groupBy(_._1)

      grouped.headOption match {
        // If there's at least one element on the list, get the first one
        case Some((laboratory, rooms)) =>

          val roomsWithComputers = rooms.map {
            // Result will have the same elements as in the above "res", so clean the already known laboratory
            groupedElements => (groupedElements._2, groupedElements._3)
          }.groupBy {
            // And group by room
            cleanedGroupedElements => cleanedGroupedElements._1
          }.map {
            // If the grouped elements follow this schema
            case (maybeRoom, groupedRoomWithComputers) =>
              (maybeRoom, groupedRoomWithComputers
                // Clean already known "maybeRoom"
                .map(_._2)
                // Group by computer
                .groupBy(_._1)
                .flatMap {
                  case (Some(computer), computerStateAndConnectedUsers) => Some((computer, computerStateAndConnectedUsers.flatMap {
                    // If there is a computer state related to this computer, clean  and package them
                    case (_, Some(state), user) => Some((state, user))
                    // If there is not, save a None
                    case _ => None
                  }))
                  // If it is not a computer here, save a None
                  case _ => None
                }
                .map { computerAndStates =>
                  (computerAndStates._1, computerAndStates._2
                    // Group states and user by state
                    .groupBy(_._1)
                    .map {
                      groupedState =>
                        // Filter nonempty connected users
                        (groupedState._1, groupedState._2.flatMap(_._2))
                    }
                    // Convert it to a sequence
                    .toSeq
                    // Sort them by the registered date
                    .sortBy(_._1.registeredDate.getTime)
                    // Reverse it to get the latest as the first element
                    .reverse
                    // And save that optional first element
                    .headOption
                    )
                }.toSeq
                // Sort computers by their IP address
                .sortBy(_._1.ip)
                )
          }.filter {
            // Filter defined rows
            row => row._1.isDefined
          }
          // Return the laboratory with its packaged rooms and computers
          Some(laboratory, roomsWithComputers)
        case e =>
          // Laboratory not found!
          None
      }
    }
  }

  override def listAll: Future[Seq[Laboratory]] = laboratoryDAO.listAll
}
