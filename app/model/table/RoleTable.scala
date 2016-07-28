package model.table

import model.Role
import slick.driver.MySQLDriver.api._

/**
  * Role table map with Slick
  *
  * @param tag Table tag
  */
class RoleTable(tag: Tag) extends Table[Role](tag, "role") {

  // All tables need the * method with the type that it was created the table with.
  override def * = (id, description) <>((Role.apply _).tupled, Role.unapply)

  // Primary key
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  // Other columns/attributes
  def description = column[String]("description")
}
