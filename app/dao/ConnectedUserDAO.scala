package dao

import java.sql.Timestamp

import com.google.inject.ImplementedBy
import dao.impl.ConnectedUserDAOImpl
import model.ConnectedUser

import scala.concurrent.Future

/**
  * Performs connected user database actions
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[ConnectedUserDAOImpl])
trait ConnectedUserDAO {

  /**
    * Adds a new connected user.
    *
    * @param user User to add
    * @return Result String
    */
  def add(user: ConnectedUser): Future[String]

  /**
    * Gets a connected user based on the computer's IP and date on which it was registered.
    *
    * @param ip    Computer's IP
    * @param date Date on which user was registered connected
    * @return Some User found
    */
  def get(ip: String, date: Timestamp): Future[Seq[ConnectedUser]]

  /**
    * Deletes a connected user from database
    *
    * @return Operation result
    */
  def delete(id: Int): Future[Int]
}
