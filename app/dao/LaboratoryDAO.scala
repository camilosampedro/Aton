package dao

import com.google.inject.ImplementedBy
import dao.impl.LaboratoryDAOImpl
import model._

import scala.concurrent.Future

/**
  * Performs Laboratory database actions
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[LaboratoryDAOImpl])
trait LaboratoryDAO {

  /**
    * Gets the laboratory with all the rooms an computers associated.
    *
    * @param id Laboratory's ID.
    * @return Found laboratory with all its rooms and computers.
    */
  def getWithChildren(id: Long): Future[Seq[(Laboratory, Option[Room], (Option[Computer], Option[ComputerState], Option[ConnectedUser]))]]


  /**
    * Adds a new laboratory to database.
    *
    * @param laboratory Laboratory to add.
    * @return Result string message.
    */
  def add(laboratory: Laboratory): Future[String]

  /**
    * Gets a laboratory by its ID.
    *
    * @param id Laboratory's ID.
    * @return Some found laboratory or None if its not found.
    */
  def get(id: Long): Future[Option[Laboratory]]

  /**
    * Removes a laboratory
    *
    * @param id Laboratory's ID.
    * @return Operation result.
    */
  def delete(id: Long): Future[Int]

  /**
    * List all the laboratories on the database.
    *
    * @return All the laboratories found.
    */
  def listAll: Future[Seq[Laboratory]]
}
