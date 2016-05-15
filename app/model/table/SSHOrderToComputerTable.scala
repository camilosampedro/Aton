package model.table

import java.sql.Timestamp

import model.SSHOrderToComputer
import slick.driver.MySQLDriver.api._

/**
  * command table map with Slick
  *
  * @param tag
  */
class SSHOrderToComputerTable(tag: Tag) extends Table[SSHOrderToComputer](tag, "ssh_order_to_computer") {

  // All tables need the * method with the type that it was created the table with.
  override def * = (computerIp, sshOrderDatetime, result, exitCode) <>(SSHOrderToComputer.tupled, SSHOrderToComputer.unapply)

  // Primary key
  def pk = primaryKey("ssh_order_to_computer_pk", (computerIp, sshOrderDatetime))

  def sshOrderDatetime = column[Timestamp]("ssh_order_datetime")

  // Other columns/attributes
  def computerIp = column[String]("computer_ip")

  def result = column[Option[String]]("result")

  def exitCode = column[Option[Int]]("exit_code")
}