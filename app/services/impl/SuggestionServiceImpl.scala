package services.impl

import com.google.inject.{Inject, Singleton}
import dao.SuggestionDAO
import model.Suggestion
import services.SuggestionService
import services.state.ActionState

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilosampedro on 16/10/16.
  */
@Singleton
class SuggestionServiceImpl @Inject()(suggestionDAO: SuggestionDAO)(implicit executionContext: ExecutionContext) extends SuggestionService {
  override def listAll: Future[Seq[Suggestion]] = suggestionDAO.listAll

  override def add(suggestion: Suggestion): Future[ActionState] = suggestionDAO.add(suggestion)

  override def delete(id: Long): Future[ActionState] = suggestionDAO.delete(id)
}
