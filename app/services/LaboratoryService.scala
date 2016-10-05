package services

import com.google.inject.ImplementedBy
import model._
import services.impl.LaboratoryServiceImpl

import scala.concurrent.Future

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[LaboratoryServiceImpl])
trait LaboratoryService {
  def get(id: Long): Future[Option[(Laboratory, Map[Option[Room], Seq[(Computer, Option[(ComputerState,Seq[ConnectedUser])])]])]]
  def listAll: Future[Seq[Laboratory]]
}
