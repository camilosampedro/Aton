package model.table

import model.Room
import slick.driver.MySQLDriver.api._

/**
  * Room table map with Slick
  *
  * @param tag Table tag
  */
class RoomTable(tag: Tag) extends Table[Room](tag, "room") {

  // Laboratory foreign key
  def laboratory = foreignKey("laboratory_id", laboratoryId, TableQuery[LaboratoryTable])(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  // All tables need the * method with the type that it was created the table with.
  override def * = (id, name, audiovisualResources, basicTools, laboratoryId) <>(Room.tupled, Room.unapply)

  // Primary key
  def id = column[Long]("id", O.PrimaryKey)

  // Other columns/attributes
  def name = column[String]("name")

  def audiovisualResources = column[Option[String]]("audiovisual_resources")

  def basicTools = column[Option[String]]("basic_tools")

  def laboratoryId = column[Long]("laboratory_id")
}
