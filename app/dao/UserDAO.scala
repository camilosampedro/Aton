package dao

import com.google.inject.ImplementedBy
import dao.impl.UserDAOImpl
import model.User
import model.json.LoginJson
import services.state.ActionState

import scala.concurrent.Future

/**
  * Controla las acciones sobre la base de datos.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[UserDAOImpl])
trait UserDAO {

  /**
    * Adiciona una UserText
    *
    * @param user User a agregar
    * @return String con el mensaje del result
    */
  def add(user: User): Future[ActionState]

  /**
    * Obtiene un UserText según el username
    *
    * @param username usernameentificador del UserText
    * @return User encontrado o None si no se encontró
    */
  def get(username: String): Future[Option[User]]

  /**
    * Elimina un UserText de la base de datos
    *
    * @param username usernameentificador del UserText
    * @return Resultado de la operación
    */
  def delete(username: String): Future[ActionState]

  /**
    * Lista todos los users en la base de datos
    *
    * @return Todos los users
    */
  def listAll: Future[Seq[User]]
}
