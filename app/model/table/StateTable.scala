package model.table

import java.sql.Timestamp

import model.{ComputerState, State}
import slick.driver.MySQLDriver.api._

/**
  * ComputerState table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class StateTable(tag: Tag) extends Table[State](tag, "computer_state") {
  // All tables need the * method with the type that it was created the table.
  def * = (id,code) <>(State.tupled, State.unapply)

  // Primary key
  def id = column[Int]("id",O.PrimaryKey)

  // Other columns/attributes
  def code = column[String]("code")
}
