package dao

import com.google.inject.ImplementedBy
import dao.impl.ComputerDAOImpl
import model.{Computer, ComputerState, ConnectedUser}

import scala.concurrent.Future

/**
  * Performs Computer database actions.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[ComputerDAOImpl])
trait ComputerDAO {
  def get(severalComputers: List[String]): Future[Seq[Computer]]

  def listAllSimple: Future[Seq[Computer]]


  /**
    * Adds a new computer
    *
    * @param computer Computer to add
    * @return Result String message
    */
  def add(computer: Computer): Future[String]

  /**
    * Gets a computer based on its IP
    *
    * @param ip Computer's IP
    * @return Some Computer found or None if its not found.
    */
  def get(ip: String): Future[Option[Computer]]

  def getWithStatus(ip: String): Future[Seq[(Computer, Option[ComputerState], Option[ConnectedUser])]]

  /**
    * Deletes a computer from database
    *
    * @param ip Computer's IP
    * @return Operation result
    */
  def delete(ip: String): Future[Int]

  /**
    * Lists all computers in the database.
    *
    * @return All computers found.
    */
  def listAll: Future[Seq[(Computer, Option[ComputerState], Option[ConnectedUser])]]

  def edit(computer: Computer): Future[Int]
}

