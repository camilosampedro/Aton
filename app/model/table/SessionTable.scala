package model.table

import java.sql.Timestamp

import model.ComputerSession
import slick.driver.MySQLDriver.api._

/**
  * ComputerSession table map with Slick
  *
  * @param tag
  */
class SessionTable(tag: Tag) extends Table[ComputerSession](tag, "computer_session") {
  // Primary key
  def pk = primaryKey("session_pk", (computerIp, connectionTime))

  // Fecha se mapea a java.sql.TimeStamp.
  // Ver: http://stackoverflow.com/questions/31351361/storing-date-and-time-into-mysql-using-slick-scala
  def connectionTime = column[Timestamp]("connection_time") //(DateMapper.utilDateToSQLTimeStamp)

  // Other columns/attributes
  def computerIp = column[String]("computer_ip")

  // Clave foránea hacia Computer
  def computer = foreignKey("session_computer_fk", computerIp, TableQuery[ComputerTable])(_.ip)

  // All tables need the * method with the type that it was created the table with.
  override def * =
    (computerIp, connectionTime, connectedUser, active) <>(ComputerSession.tupled, ComputerSession.unapply)

  def connectedUser = column[String]("connected_user")

  def active = column[Boolean]("active")
}
