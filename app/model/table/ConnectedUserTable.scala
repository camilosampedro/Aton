package model.table

import java.sql.Timestamp

import model.{ComputerState, ConnectedUser}
import slick.driver.H2Driver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

/**
  * ComputerSession table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class ConnectedUserTable(tag: Tag) extends Table[ConnectedUser](tag, "CONNECTED_USER") {
  // Primary key
  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  // Date maps to java.sql.TimeStamp.
  // Ver: http://stackoverflow.com/questions/31351361/storing-date-and-time-into-mysql-using-slick-scala
  def computerStateRegisteredDate: Rep[Timestamp] = column[Timestamp]("COMPUTER_STATE_REGISTERED_DATE")

  // Other columns/attributes
  def computerStateComputerIp: Rep[String] = column[String]("COMPUTER_STATE_COMPUTER_IP")

  // Foreign key to Computer
  def computer: ForeignKeyQuery[ComputerStateTable, ComputerState] =
    foreignKey("CONNECTEC_USER_COMPUTER_STATE", (computerStateComputerIp, computerStateRegisteredDate),
      TableQuery[ComputerStateTable])(x => (x.computerIp, x.registeredDate), onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade)

  // All tables need the * method with the type that it was created the table with.
  override def * : ProvenShape[ConnectedUser] =
    (id, username, computerStateComputerIp, computerStateRegisteredDate) <> (ConnectedUser.tupled, ConnectedUser.unapply)

  def username: Rep[String] = column[String]("USERNAME")
}
