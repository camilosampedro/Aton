package model.table

import java.sql.Timestamp

import model.{ComputerState, State}
import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
  * ComputerState table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class StateTable(tag: Tag) extends Table[State](tag, "COMPUTER_STATE") {
  // All tables need the * method with the type that it was created the table.
  def * : ProvenShape[State] = (id,code) <>(State.tupled, State.unapply)

  // Primary key
  def id: Rep[Int] = column[Int]("ID",O.PrimaryKey)

  // Other columns/attributes
  def code: Rep[String] = column[String]("CODE")
}
