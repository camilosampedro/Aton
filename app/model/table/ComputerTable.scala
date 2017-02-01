package model.table

import model.{Computer, Room}
import slick.driver.H2Driver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

import scala.language.postfixOps

/**
  * Computer table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class ComputerTable(tag: Tag) extends Table[Computer](tag, "COMPUTER") {

  // Other columns/attributes
  def name: Rep[Option[String]] = column[Option[String]]("NAME")

  // Room foreign key
  def room: ForeignKeyQuery[RoomTable, Room] =
    foreignKey("ROOM_ID", roomId, TableQuery[RoomTable])(_.id?, onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade)

  // All tables need the * method with the type that it was created the table.
  override def * : ProvenShape[Computer] =
    (ip, name, SSHUser, SSHPassword, description, roomId) <>(Computer.tupled, Computer.unapply)

  // PrimaryKey
  def ip: Rep[String] = column[String]("IP", O.PrimaryKey)

  def SSHUser: Rep[String] = column[String]("SSH_USER")

  def SSHPassword: Rep[String] = column[String]("SSH_PASSWORD")

  def description: Rep[Option[String]] = column[Option[String]]("DESCRIPTION")

  def roomId: Rep[Option[Long]] = column[Option[Long]]("ROOM_ID")
}
