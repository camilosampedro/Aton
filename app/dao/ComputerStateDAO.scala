package dao

import java.sql.Timestamp

import com.google.inject.ImplementedBy
import dao.impl.ComputerStateDAOImpl
import model.ComputerState
import services.state.ActionState

import scala.concurrent.Future

/**
  * Performs ComputerState database actions
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[ComputerStateDAOImpl])
trait ComputerStateDAO {

  /**
    * Adds a new ComputerState
    *
    * @param computerState ComputerState to add
    * @return Result String
    */
  def add(computerState: ComputerState): Future[ActionState]

  /**
    * Gets a ComputerState using its two identifiers: IP address and date
    *
    * @param ip    Computer's IP
    * @param date Date on which the ComputerState was added
    * @return Some ComputerState found or None if its not found
    */
  def get(ip: String, date: Timestamp): Future[Option[ComputerState]]

  /**
    * Deletes a ComputerState from the database
    *
    * @param ip    Computer's IP
    * @param date Date on which the ComputerState was added
    * @return Operation result
    */
  def delete(ip: String, date: Timestamp): Future[Int]

  /**
    * List all ComputerStates on the database
    *
    * @return All ComputerStates found.
    */
  def listAll: Future[Seq[ComputerState]]
}
