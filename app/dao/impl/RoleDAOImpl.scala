package dao.impl

import javax.inject.Inject

import com.google.inject.Singleton
import dao.RoleDAO
import model.Role
import model.table.RoleTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Performs role database actions
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param dbConfigProvider Database manager injected
  */
@Singleton
class RoleDAOImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider) extends RoleDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._


  /**
    * Table of Roles
    */
  implicit val roles = TableQuery[RoleTable]

  /**
    * Adds a new Role
    *
    * @param role Role to add
    * @return Result String
    */
  override def add(role: Role): Future[String] = {
    // Se realiza un insert y por cada insert se crea un String
    db.run(roles += role).map(res => "Role agregado correctamente").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  /**
    * Gets a Role with its ID
    *
    * @param roleId Role's ID
    * @return Some Role found, None if its not found.
    */
  override def get(roleId: Int): Future[Option[Role]] = {
    // Se realiza un select * from roleText where id = $id
    db.run(search(roleId).result.headOption)
  }

  private def search(id: Int) = roles.filter(_.id === id)

  /**
    * Deletes a Role from database.
    *
    * @param roleId Role's ID
    * @return Operation result
    */
  override def delete(roleId: Int): Future[Int] = {
    db.run(search(roleId).delete)
  }

  /**
    * List all the ROles on the database.
    *
    * @return All Roles found.
    */
  override def listAll: Future[Seq[Role]] = {
    db.run(roles.result)
  }
}
