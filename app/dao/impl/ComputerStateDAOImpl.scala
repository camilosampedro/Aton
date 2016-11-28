package dao.impl

import java.sql.Timestamp
import javax.inject.Inject

import com.google.inject.Singleton
import dao.ComputerStateDAO
import model.ComputerState
import model.table.ComputerStateTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Performs all ComputerState database operations
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param dbConfigProvider Database manager injected
  */
@Singleton
class ComputerStateDAOImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider, equipoDAOImpl: ComputerDAOImpl) extends ComputerStateDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._


  /**
    * Table with all the computer states
    */
  implicit val computerStates = TableQuery[ComputerStateTable]

  /**
    * Adds a new ComputerState
    *
    * @param computerState ComputerState to add
    * @return Result String
    */
  override def add(computerState: ComputerState): Future[String] = {
    db.run(computerStates += computerState).map(res => "ComputerState agregado correctamente").recover {
      case ex: Exception => play.Logger.error("Error saving: " + computerState,ex)
        "Error"
    }
  }

  /**
    * Gets a ComputerState using its two identifiers: IP address and date
    *
    * @param ip    Computer's IP
    * @param date Date on which the ComputerState was added
    * @return Some ComputerState found or None if its not found
    */
  override def get(ip: String, date: Timestamp): Future[Option[ComputerState]] = {
    db.run(search(ip, date).result.headOption)
  }

  /**
    * Deletes a ComputerState from the database
    *
    * @param ip    Computer's IP
    * @param date Date on which the ComputerState was added
    * @return Operation result
    */
  override def delete(ip: String, date: Timestamp): Future[Int] = {
    db.run(search(ip, date).delete)
  }

  private def search(ip: String, fecha: Timestamp) = computerStates.filter(a => a.computerIp === ip && a.registeredDate == fecha)

  /**
    * List all ComputerStates on the database
    *
    * @return All ComputerStates found.
    */
  override def listAll: Future[Seq[ComputerState]] = {
    db.run(computerStates.result)
  }
}
