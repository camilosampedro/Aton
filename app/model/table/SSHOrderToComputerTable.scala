package model.table

import java.sql.Timestamp

import model.SSHOrderToComputer
import slick.driver.H2Driver.api._
import slick.profile.SqlProfile.ColumnOption.SqlType

/**
  * SSHOrderToComputer table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class SSHOrderToComputerTable(tag: Tag) extends Table[SSHOrderToComputer](tag, "ssh_order_to_computer") {

  // All tables need the * method with the type that it was created the table with.
  override def * = (computerIp, sshOrderId, sentDateTime, result, exitCode) <>(SSHOrderToComputer.tupled, SSHOrderToComputer.unapply)

  // Primary key
  def pk = primaryKey("ssh_order_to_computer_pk", (computerIp, sshOrderId))

  def sentDateTime = column[Timestamp]("sent_datetime")

  def sshOrderId = column[Long]("ssh_order_id")

  def sshOrder = foreignKey("ssh_order_to_computer_ssh_order", sshOrderId, TableQuery[SSHOrderTable])(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
  def computer = foreignKey("computer", computerIp, TableQuery[ComputerTable])(_.ip, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  // Other columns/attributes
  def computerIp = column[String]("computer_ip")

  def result = column[Option[String]]("result")

  def exitCode = column[Option[Int]]("exit_code")
}