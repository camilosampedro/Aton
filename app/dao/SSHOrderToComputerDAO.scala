package dao

import java.sql.Timestamp

import com.google.inject.ImplementedBy
import dao.impl.SSHOrderToComputerDAOImpl
import model.SSHOrderToComputer

import scala.concurrent.Future

/**
  * Perform operations with the database.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[SSHOrderToComputerDAOImpl])
trait SSHOrderToComputerDAO {
  def update(resultSSHOrder: SSHOrderToComputer): Future[Int]


  /**
    * Adds a new SSHOrder.
    *
    * @param sshOrderToComputer Order to add.
    * @return Result String message.
    */
  def add(sshOrderToComputer: SSHOrderToComputer): Future[String]

  /**
    * Gets a SSHOrder using its ID.
    *
    * @param id SSHOrder's id.
    * @return Some SSHOrder found or None if it's not found.
    */
  def get(id: Timestamp): Future[Option[SSHOrderToComputer]]

  /**
    * Deletes an SSHOrder from database
    *
    * @param id SSHOrder's id
    * @return Operation result
    */
  def delete(id: Timestamp): Future[Int]

  /**
    * List all SSHOrders from database
    *
    * @return All SSHOrders found.
    */
  def listAll: Future[Seq[SSHOrderToComputer]]
}
