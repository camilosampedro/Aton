package model.table

import java.sql.Timestamp

import model.{Computer, ComputerState, State}
import slick.driver.H2Driver.api._
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

/**
  * ComputerState table map with Slick
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class ComputerStateTable(tag: Tag) extends Table[ComputerState](tag, "COMPUTER_STATE") {
  // All tables need the * method with the type that it was created the table.
  def * : ProvenShape[ComputerState] =
    (computerIp, registeredDate, stateId, operatingSystem, mac) <>(ComputerState.tupled, ComputerState.unapply)

  // Primary key
  def pk: PrimaryKey = primaryKey("COMPUTER_STATE_PK", (computerIp, registeredDate))

  // Other columns/attributes
  def computerIp: Rep[String] = column[String]("COMPUTER_IP")

  // Date mapped to java.sql.TimeStamp.
  // See: http://stackoverflow.com/questions/31351361/storing-date-and-time-into-mysql-using-slick-scala
  def registeredDate: Rep[Timestamp] = column[Timestamp]("REGISTERED_DATE")

  def stateId: Rep[Int] = column[Int]("STATE_ID")

  def mac: Rep[Option[String]] = column[Option[String]]("MAC")

  // Computer foreign key
  def computer: ForeignKeyQuery[ComputerTable, Computer] =
    foreignKey("COMPUTER_STATE_COMPUTER_FK", computerIp, TableQuery[ComputerTable])(_.ip,
      onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  def state: ForeignKeyQuery[StateTable, State] =
    foreignKey("STATE_FK", stateId, TableQuery[StateTable])(_.id, onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade)

  def operatingSystem: Rep[Option[String]] = column[Option[String]]("OPERATING_SYSTEM")
}
