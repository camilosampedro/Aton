package model.table

import java.sql.Timestamp

import model.ConnectedUser
import slick.driver.MySQLDriver.api._

/**
  * ComputerSession table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class ConnectedUserTable(tag: Tag) extends Table[ConnectedUser](tag, "connected_user") {
  // Primary key
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  // Date maps to java.sql.TimeStamp.
  // Ver: http://stackoverflow.com/questions/31351361/storing-date-and-time-into-mysql-using-slick-scala
  def computerStateRegisteredDate = column[Timestamp]("computer_state_registered_date") //(DateMapper.utilDateToSQLTimeStamp)

  // Other columns/attributes
  def computerStateComputerIp = column[String]("computer_state_computer_ip")

  // Foreign key to Computer
  def computer = foreignKey("connected_user_computer_state", (computerStateComputerIp, computerStateRegisteredDate), TableQuery[ComputerStateTable])(x => (x.computerIp, x.registeredDate), onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  // All tables need the * method with the type that it was created the table with.
  override def * =
  (id, username, computerStateComputerIp, computerStateRegisteredDate) <> (ConnectedUser.tupled, ConnectedUser.unapply)

  def username = column[String]("username")
}
