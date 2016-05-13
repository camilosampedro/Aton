package model.table

import model.SSHOrder
import slick.driver.MySQLDriver.api._

/**
  * command table map with Slick
  *
  * @param tag
  */
class SSHOrderTable(tag: Tag) extends Table[SSHOrder](tag, "ssh_order") {

  // All tables need the * method with the type that it was created the table with.
  override def * =
    (id, superuser, interrupt, command, result, exitCode) <>(SSHOrder.tupled, SSHOrder.unapply)

  // Primary key
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  // Other columns/attributes
  def superuser = column[Boolean]("superuser")

  def interrupt = column[Boolean]("interrupt")

  def command = column[String]("command")

  def result = column[String]("result")

  def exitCode = column[Int]("exit_code")
}