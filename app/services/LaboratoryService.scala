package services

import com.google.inject.ImplementedBy
import model._
import services.impl.LaboratoryServiceImpl
import services.state.ActionState

import scala.concurrent.Future

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[LaboratoryServiceImpl])
trait LaboratoryService {
  def get(id: Long): Future[Option[(Laboratory, Map[Room, Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]])]]
  def getSingle(id: Long): Future[Option[Laboratory]]
  def listAll: Future[Seq[Laboratory]]
  def add(laboratory: Laboratory): Future[ActionState]
  def delete(id: Long): Future[ActionState]
}
