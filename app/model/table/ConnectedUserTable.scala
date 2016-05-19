package model.table

import java.sql.Timestamp

import model.ConnectedUser
import slick.driver.MySQLDriver.api._

/**
  * ComputerSession table map with Slick
  *
  * @param tag
  */
class ConnectedUserTable(tag: Tag) extends Table[ConnectedUser](tag, "connected_user") {
  // Primary key
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  // Fecha se mapea a java.sql.TimeStamp.
  // Ver: http://stackoverflow.com/questions/31351361/storing-date-and-time-into-mysql-using-slick-scala
  def computerStateRegisteredDate = column[Timestamp]("computer_state_registered_date") //(DateMapper.utilDateToSQLTimeStamp)

  // Other columns/attributes
  def computerStateComputerIp = column[String]("computer_state_computer_ip")

  // Clave foránea hacia Computer
  def computer = foreignKey("connected_user_computer_state", (computerStateComputerIp,computerStateRegisteredDate), TableQuery[ComputerStateTable])(x=>(x.computerIp,x.registeredDate))

  // All tables need the * method with the type that it was created the table with.
  override def * =
    (id,username,computerStateComputerIp, computerStateRegisteredDate) <>(ConnectedUser.tupled, ConnectedUser.unapply)

  def username = column[String]("username")
}
