package model.table

import model.Laboratory
import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

/**
  * Laboratory table map with Slick
  *
  * @param tag
  */
class LaboratoryTable(tag: Tag) extends Table[Laboratory](tag, "laboratory") {

  // All tables need the * method with the type that it was created the table with.
  override def * : ProvenShape[Laboratory] =
    (id, name, location, administration) <>(Laboratory.tupled, Laboratory.unapply)

  // Primary key
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  // Other columns/attributes
  def name = column[String]("name")

  def location = column[Option[String]]("location")

  def administration = column[Option[String]]("administration")
}
