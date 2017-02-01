package dao.impl

import javax.inject.Inject

import com.google.inject.Singleton
import dao.UserDAO
import model.User
import model.json.LoginJson
import model.table.UserTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import services.state.ActionState
import services.state
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Se encarga de implementar las acciones sobre la base de datos
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param dbConfigProvider Inyección del gestor de la base de datos
  */
@Singleton
class UserDAOImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider) extends UserDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._


  /**
    * Tabla con "todos los users", similar a select * from userText
    */
  implicit val users = TableQuery[UserTable]

  /**
    * Adiciona un userText
    *
    * @param user User a agregar
    * @return String con el mensaje del result
    */
  override def add(user: User): Future[ActionState] = {
    // Se realiza un insert y por cada insert se crea un String
    db.run(users += user).map(_ => state.ActionCompleted).recover {
      case ex: Exception =>
        play.Logger.error("User adding error",ex)
        state.Failed
    }
  }

  /**
    * Obtiene un userText según el username
    *
    * @param username Identificador del userText
    * @return User encontrado o None si no se encontró
    */
  override def get(username: String): Future[Option[User]] = {
    // Se realiza un select * from userText where username = $id
    db.run(search(username).result.headOption)
  }

  private def search(username: String) = users.filter(_.username === username)

  /**
    * Elimina un userText de la base de datos
    *
    * @param username Identificador del userText
    * @return Resultado de la operación
    */
  override def delete(username: String): Future[ActionState] = {
    db.run(search(username).delete).map {
      case 0 => state.ActionCompleted
      case _ => state.Failed
    }
  }

  /**
    * Lista todas los users en la base de datos
    *
    * @return Todos los users
    */
  override def listAll: Future[Seq[User]] = {
    db.run(users.result)
  }
}
