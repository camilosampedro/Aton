package dao

import com.google.inject.ImplementedBy
import dao.impl.RoomDAOImpl
import model.Room
import services.state.ActionState

import scala.concurrent.Future

/**
  * Controls all room database operations
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[RoomDAOImpl])
trait RoomDAO {
  def getByLaboratory(id: Long): Future[Seq[Room]]


  /**
    * Adds a new room
    *
    * @param room Room to be added
    * @return String result message
    */
  def add(room: Room): Future[ActionState]

  /**
    * Gets a room by its ID
    *
    * @param id Room ID
    * @return Some found room or None otherwise
    */
  def get(id: Long): Future[Option[Room]]

  /**
    * Deletes a room by its ID
    *
    * @param id Room ID
    * @return Operation result
    */
  def delete(id: Long): Future[ActionState]

  /**
    * List all the rooms in the database
    *
    * @return All the rooms
    */
  def listAll: Future[Seq[Room]]
}
