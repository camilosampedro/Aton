package model.table

import java.sql.Timestamp

import model.{Suggestion, User}
import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

/**
  * Mapeo de la tabla user con Slick
  *
  * @param tag
  */
class UserTable(tag: Tag) extends Table[User](tag, "user") {

  // All tables need the * method with the type that it was created the table with.
  override def * = (username, password, name, role)<>(User.tupled, User.unapply)

  // Primary key
  def username = column[String]("username", O.PrimaryKey)

  // Other columns/attributes
  def password = column[String]("password")
  def name = column[Option[String]]("name")
  def role = column[Int]("role")
  def role_fk = foreignKey("usuarioweb_rolusuario_fk", role, TableQuery[RoleTable])(_.id)
}
