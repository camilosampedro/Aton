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
  * Se encarga de implementar las acciones sobre la base de datos
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param dbConfigProvider Inyección del gestor de la base de datos
  */
@Singleton
class RoleDAOImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider) extends RoleDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._


  /**
    * Tabla con "todos los roles", similar a select * from roleText
    */
  implicit val roles = TableQuery[RoleTable]

  /**
    * Adiciona un roleText
    *
    * @param role Role a agregar
    * @return String con el mensaje del result
    */
  override def add(role: Role): Future[String] = {
    // Se realiza un insert y por cada insert se crea un String
    db.run(roles += role).map(res => "Role agregado correctamente").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  /**
    * Obtiene un roleText según el id
    *
    * @param id Identificador del roleText
    * @return Role encontrado o None si no se encontró
    */
  override def get(id: Int): Future[Option[Role]] = {
    // Se realiza un select * from roleText where id = $id
    db.run(search(id).result.headOption)
  }

  private def search(id: Int) = roles.filter(_.id === id)

  /**
    * Elimina un roleText de la base de datos
    *
    * @param id Identificador del roleText
    * @return Resultado de la operación
    */
  override def delete(id: Int): Future[Int] = {
    db.run(search(id).delete)
  }

  /**
    * Lista todas los roles en la base de datos
    *
    * @return Todos los roles
    */
  override def listAll: Future[Seq[Role]] = {
    db.run(roles.result)
  }
}
