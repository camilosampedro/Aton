package dao

import com.google.inject.ImplementedBy
import dao.impl.SuggestionDAOImpl
import model.Suggestion

import scala.concurrent.Future

/**
  * Controla las acciones sobre la base de datos.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[SuggestionDAOImpl])
trait SuggestionDAO {

  /**
    * Adiciona una suggestionText
    *
    * @param sugerencia Suggestion a agregar
    * @return String con el mensaje del result
    */
  def add(sugerencia: Suggestion): Future[String]

  /**
    * Obtiene un suggestionText según el id
    *
    * @param id Identificador del suggestionText
    * @return Suggestion encontrado o None si no se encontró
    */
  def get(id: Long): Future[Option[Suggestion]]

  /**
    * Elimina un suggestionText de la base de datos
    *
    * @param id Identificador del suggestionText
    * @return Resultado de la operación
    */
  def delete(id: Long): Future[Int]

  /**
    * Lista todos los sugerencias en la base de datos
    *
    * @return Todos los sugerencias
    */
  def listAll: Future[Seq[Suggestion]]
}
