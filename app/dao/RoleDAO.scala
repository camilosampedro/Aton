package dao

import com.google.inject.ImplementedBy
import dao.impl.RoleDAOImpl
import model.Role

import scala.concurrent.Future

/**
  * Performs role database actions
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[RoleDAOImpl])
trait RoleDAO {

  /**
    * Adds a new Role
    *
    * @param role Role to add
    * @return Result String
    */
  def add(role: Role): Future[String]

  /**
    * Gets a Role with its ID
    *
    * @param roleId Role's ID
    * @return Some Role found, None if its not found.
    */
  def get(roleId: Int): Future[Option[Role]]


  /**
    * Deletes a Role from database.
    *
    * @param roleId Role's ID
    * @return Operation result
    */
  def delete(roleId: Int): Future[Int]

  /**
    * List all the ROles on the database.
    *
    * @return All Roles found.
    */
  def listAll: Future[Seq[Role]]
}
