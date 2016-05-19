package model.table

import java.sql.Timestamp

import model.SSHOrder
import slick.driver.MySQLDriver.api._
import slick.profile.SqlProfile.ColumnOption.SqlType

/**
  * command table map with Slick
  *
  * @param tag
  */
class SSHOrderTable(tag: Tag) extends Table[SSHOrder](tag, "ssh_order") {

  // All tables need the * method with the type that it was created the table with.
  override def * =
    (id, sentDatetime, superuser, interrupt, command, webUser) <>(SSHOrder.tupled, SSHOrder.unapply)

  // Primary key
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)


  // Other columns/attributes
  def superuser = column[Boolean]("superuser")

  def sentDatetime = column[Timestamp]("sent_datetime", SqlType("timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP"))

  def command = column[String]("command")

  def interrupt = column[Boolean]("interrupt")

  def webUser = column[String]("web_user")
}