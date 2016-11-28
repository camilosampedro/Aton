package dao

import com.google.inject.ImplementedBy
import dao.impl.SuggestionDAOImpl
import model.Suggestion
import services.state.ActionState

import scala.concurrent.Future

/**
  * Controla las acciones sobre la base de datos.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[SuggestionDAOImpl])
trait SuggestionDAO {

  /**
    * Adds a new suggestion
    *
    * @param suggestion Suggestion to be added
    * @return String result message
    */
  def add(suggestion: Suggestion): Future[ActionState]

  /**
    * Gets a suggestion by its ID
    *
    * @param id Suggestion ID
    * @return Some found suggestion or None
    */
  def get(id: Long): Future[Option[Suggestion]]

  /**
    * Deletes a suggestion from database
    *
    * @param id Suggestion ID
    * @return Operation result
    */
  def delete(id: Long): Future[ActionState]

  /**
    * Lists all suggestions on the database
    *
    * @return All the suggestions
    */
  def listAll: Future[Seq[Suggestion]]
}
