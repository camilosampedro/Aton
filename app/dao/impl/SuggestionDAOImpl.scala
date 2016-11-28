package dao.impl

import javax.inject.Inject

import com.google.inject.Singleton
import dao.SuggestionDAO
import model.Suggestion
import model.table.SuggestionTable
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
class SuggestionDAOImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider) extends SuggestionDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._


  /**
    * Tabla con "todos los sugerencias", similar a select * from suggestionText
    */
  implicit val sugerencias = TableQuery[SuggestionTable]

  /**
    * Adiciona un suggestionText
    *
    * @param sugerencia Suggestion a agregar
    * @return String con el mensaje del result
    */
  override def add(sugerencia: Suggestion): Future[ActionState] = {
    // Se realiza un insert y por cada insert se crea un String
    db.run(sugerencias += sugerencia).map(_ => state.ActionCompleted).recover {
      case ex: Exception =>
        play.Logger.error("Suggestion adding error", ex)
        state.Failed
    }
  }

  /**
    * Obtiene un suggestionText según el id
    *
    * @param id Identificador del suggestionText
    * @return Suggestion encontrado o None si no se encontró
    */
  override def get(id: Long): Future[Option[Suggestion]] = {
    // Se realiza un select * from suggestionText where id = $id
    db.run(search(id).result.headOption)
  }

  private def search(id: Long) = sugerencias.filter(_.id === id)

  /**
    * Elimina un suggestionText de la base de datos
    *
    * @param id Identificador del suggestionText
    * @return Resultado de la operación
    */
  override def delete(id: Long): Future[ActionState] = {
    db.run(search(id).delete).map {
      case 0 => state.ActionCompleted
      case _ => state.Failed
    }
  }

  /**
    * Lista todas los sugerencias en la base de datos
    *
    * @return Todos los sugerencias
    */
  override def listAll: Future[Seq[Suggestion]] = {
    db.run(sugerencias.result)
  }
}
