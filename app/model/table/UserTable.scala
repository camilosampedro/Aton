package model.table

import model.{Role, User}
import slick.driver.H2Driver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

/**
  * User table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class UserTable(tag: Tag) extends Table[User](tag, "USER") {

  // All tables need the * method with the type that it was created the table with.
  override def * : ProvenShape[User] = (username, password, name, role) <>(User.tupled, User.unapply)

  // Primary key
  def username: Rep[String] = column[String]("USERNAME", O.PrimaryKey)

  // Other columns/attributes
  def password: Rep[String] = column[String]("PASSWORD")

  def name: Rep[Option[String]] = column[Option[String]]("NAME")

  def role: Rep[Int] = column[Int]("ROLE")

  def role_fk: ForeignKeyQuery[RoleTable, Role] = foreignKey("USUARIOWEB_ROLUSUARIO_FK", role, TableQuery[RoleTable])(_.id)
}
