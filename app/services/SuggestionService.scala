package services

import com.google.inject.ImplementedBy
import model.Suggestion
import services.impl.SuggestionServiceImpl
import services.state.ActionState

import scala.concurrent.Future

/**
  * Created by camilosampedro on 16/10/16.
  */
@ImplementedBy(classOf[SuggestionServiceImpl])
trait SuggestionService {
  def listAll: Future[Seq[Suggestion]]
  def add(suggestion: Suggestion): Future[ActionState]
  def delete(id: Long): Future[ActionState]
}
