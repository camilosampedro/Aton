package model.table

import model.Laboratory
import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
  * Laboratory table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag.
  */
class LaboratoryTable(tag: Tag) extends Table[Laboratory](tag, "LABORATORY") {

  // All tables need the * method with the type that it was created the table with.
  override def * : ProvenShape[Laboratory] =
  (id, name, location, administration) <> (Laboratory.tupled, Laboratory.unapply)

  // Primary key
  def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  // Other columns/attributes
  def name: Rep[String] = column[String]("NAME")

  def location: Rep[Option[String]] = column[Option[String]]("LOCATION")

  def administration: Rep[Option[String]] = column[Option[String]]("ADMINISTRATION")
}
