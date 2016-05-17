package services

import com.google.inject.ImplementedBy
import model.Computer
import services.impl.ComputerServiceImpl

import scala.concurrent.Future

/**
  * Created by camilo on 14/05/16.
  */
@ImplementedBy(classOf[ComputerServiceImpl])
trait ComputerService {
  def add(computer: Computer, username: String): Future[Int]
}
