package services

import com.google.inject.ImplementedBy
import model.{Computer, ComputerState}
import services.impl.ComputerServiceImpl

import scala.concurrent.Future

/**
  * Created by camilo on 14/05/16.
  */
@ImplementedBy(classOf[ComputerServiceImpl])
trait ComputerService {
  def listAll: Future[Seq[(Computer,Option[ComputerState])]]

  def add(computer: Computer): Future[String]
  def edit(computer: Computer): Future[Int]
}
