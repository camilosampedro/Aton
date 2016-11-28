package model.table

import model.User
import slick.driver.MySQLDriver.api._

/**
  * User table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class UserTable(tag: Tag) extends Table[User](tag, "user") {

  // All tables need the * method with the type that it was created the table with.
  override def * = (username, password, name, role) <>(User.tupled, User.unapply)

  // Primary key
  def username = column[String]("username", O.PrimaryKey)

  // Other columns/attributes
  def password = column[String]("password")

  def name = column[Option[String]]("name")

  def role = column[Int]("role")

  def role_fk = foreignKey("usuarioweb_rolusuario_fk", role, TableQuery[RoleTable])(_.id)
}
