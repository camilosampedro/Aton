package dao

import model.User

import scala.concurrent.Future

/**
  * Created by camilo on 15/05/16.
  */
object UserDAO {
  def add(user: User) = ???
  def findOneByUsername(username: String): User = ???
}
