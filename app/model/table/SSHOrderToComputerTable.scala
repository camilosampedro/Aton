package model.table

import java.sql.Timestamp

import model.{Computer, SSHOrder, SSHOrderToComputer}
import slick.driver.H2Driver.api._
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}
import slick.profile.SqlProfile.ColumnOption.SqlType

/**
  * SSHOrderToComputer table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class SSHOrderToComputerTable(tag: Tag) extends Table[SSHOrderToComputer](tag, "SSH_ORDER_TO_COMPUTER") {

  // All tables need the * method with the type that it was created the table with.
  override def * : ProvenShape[SSHOrderToComputer] = (computerIp, sshOrderId, sentDateTime, result, exitCode) <>(SSHOrderToComputer.tupled, SSHOrderToComputer.unapply)

  // Primary key
  def pk: PrimaryKey = primaryKey("SSH_ORDER_TO_COMPUTER_PK", (computerIp, sshOrderId))

  def sentDateTime: Rep[Timestamp] = column[Timestamp]("SENT_DATETIME")

  def sshOrderId: Rep[Long] = column[Long]("SSH_ORDER_ID")

  def sshOrder: ForeignKeyQuery[SSHOrderTable, SSHOrder] =
    foreignKey("SSH_ORDER_TO_COMPUTER_SSH_ORDER", sshOrderId, TableQuery[SSHOrderTable])(_.id,
      onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  def computer: ForeignKeyQuery[ComputerTable, Computer] =
    foreignKey("COMPUTER", computerIp, TableQuery[ComputerTable])(_.ip,
      onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  // Other columns/attributes
  def computerIp: Rep[String] = column[String]("COMPUTER_IP")

  def result: Rep[Option[String]] = column[Option[String]]("RESULT")

  def exitCode: Rep[Option[Int]] = column[Option[Int]]("EXIT_CODE")
}