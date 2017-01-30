package dao.impl

import com.google.inject.{Inject, Singleton}
import dao.DatabaseInitializer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.H2Driver.api._
import slick.profile.SqlAction

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by camilosampedro on 8/12/16.
  */
@Singleton
class DatabaseInitializerImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends DatabaseInitializer with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def createTables = {
    val source = scala.io.Source.fromFile("conf/db/create.sql")
    val query = Try {
      source.mkString
    } match {
      case Success(string) =>
        sqlu"#$string"
      case Failure(e: Throwable) => play.Logger.error("Error reading conf/db/create.sql file",e)
        sqlu""""""
    }
    source.close()
    query

  }

  override def initialize(): Future[Int] = db.run {
    createTables
  }
}
