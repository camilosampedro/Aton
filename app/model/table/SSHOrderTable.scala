package model.table

import java.sql.Timestamp

import model.SSHOrder
import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape
import slick.profile.SqlProfile.ColumnOption.SqlType

/**
  * SSHOrder table map with Slick
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param tag Table tag
  */
class SSHOrderTable(tag: Tag) extends Table[SSHOrder](tag, "SSH_ORDER") {

  // All tables need the * method with the type that it was created the table with.
  override def * : ProvenShape[SSHOrder] =
    (id, sentDatetime, superuser, interrupt, command, webUser) <>(SSHOrder.tupled, SSHOrder.unapply)

  // Primary key
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)


  // Other columns/attributes
  def superuser: Rep[Boolean] = column[Boolean]("SUPERUSER")

  def sentDatetime: Rep[Timestamp] = column[Timestamp]("SENT_DATETIME")

  def command: Rep[String] = column[String]("COMMAND")

  def interrupt: Rep[Boolean] = column[Boolean]("INTERRUPT")

  def webUser: Rep[String] = column[String]("WEB_USER")
}