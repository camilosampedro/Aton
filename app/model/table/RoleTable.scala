package model.table

import model.Role
import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
  * Role table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class RoleTable(tag: Tag) extends Table[Role](tag, "ROLE") {

  // All tables need the * method with the type that it was created the table with.
  override def * : ProvenShape[Role] = (id, description) <>((Role.apply _).tupled, Role.unapply)

  // Primary key
  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  // Other columns/attributes
  def description: Rep[String] = column[String]("DESCRIPTION")
}
