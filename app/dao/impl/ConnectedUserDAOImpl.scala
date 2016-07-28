package dao.impl

import java.sql.Timestamp
import javax.inject.Inject

import com.google.inject.Singleton
import dao.ConnectedUserDAO
import model.ConnectedUser
import model.table.ConnectedUserTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Performs connected user database actions
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param dbConfigProvider Database manager injected
  */
@Singleton
class ConnectedUserDAOImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider, equipoDAOImpl: ComputerDAOImpl) extends ConnectedUserDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._


  /**
    * Connected user table
    */
  implicit val connectedUsers = TableQuery[ConnectedUserTable]

  /**
    * Adds a new connected user.
    *
    * @param user User to add
    * @return Result String
    */
  override def add(user: ConnectedUser): Future[String] = {
    db.run(connectedUsers += user).map(res => "ComputerSession added").recover {
      case ex: Exception => play.Logger.error("Error saving: " + user,ex)
        "Error"
    }
  }

  /**
    * Gets a connected user based on the computer's IP and date on which it was registered.
    *
    * @param ip    Computer's IP
    * @param date Date on which user was registered connected
    * @return Some User found
    */
  override def get(ip: String, date: Timestamp): Future[Seq[ConnectedUser]] = {
    db.run(search(ip, date).result)
  }

  /**
    * Deletes a connected user from database
    *
    * @return Operation result
    */
  override def delete(id: Int): Future[Int] = {
    db.run(connectedUsers.filter(_.id===id).delete)
  }

  private def search(ip: String, date: Timestamp) = connectedUsers.filter(a => a.computerStateComputerIp === ip && a.computerStateRegisteredDate == date)
}
