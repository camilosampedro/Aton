package dao

import com.google.inject.ImplementedBy
import dao.impl.RoleDAOImpl
import model.Role

import scala.concurrent.Future

/**
  * Controla las acciones sobre la base de datos.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[RoleDAOImpl])
trait RoleDAO {

  /**
    * Adiciona una RoleText
    *
    * @param role Role a agregar
    * @return String con el mensaje del result
    */
  def add(role: Role): Future[String]

  /**
    * Obtiene un RoleText según el roleId
    *
    * @param roleId roleIdentificador del RoleText
    * @return Role encontrado o None si no se encontró
    */
  def get(roleId: Int): Future[Option[Role]]


  /**
    * Elimina un RoleText de la base de datos
    *
    * @param roleId roleIdentificador del RoleText
    * @return Resultado de la operación
    */
  def delete(roleId: Int): Future[Int]

  /**
    * Lista todos los roles en la base de datos
    *
    * @return Todos los roles
    */
  def listAll: Future[Seq[Role]]
}
