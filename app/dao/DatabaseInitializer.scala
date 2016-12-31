package dao

import com.google.inject.ImplementedBy
import dao.impl.DatabaseInitializerImpl

import scala.concurrent.Future

/**
  * Created by camilosampedro on 8/12/16.
  */
@ImplementedBy(classOf[DatabaseInitializerImpl])
trait DatabaseInitializer {
  def initialize(): Future[Int]
}
