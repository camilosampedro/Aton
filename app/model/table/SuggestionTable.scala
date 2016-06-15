package model.table

import java.sql.Timestamp

import model.Suggestion
import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

/**
  * Mapeo de la tabla Suggestion con Slick
  *
  * @param tag
  */
class SuggestionTable(tag: Tag) extends Table[Suggestion](tag, "suggestion") {

  // All tables need the * method with the type that it was created the table with.
  override def * : ProvenShape[Suggestion] =
    (id, suggestionText, registeredDate, username) <>(Suggestion.tupled, Suggestion.unapply)

  // Primary key
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  // Other columns/attributes
  def suggestionText = column[Option[String]]("suggestion_text")

  def registeredDate = column[Timestamp]("registered_date") //(DateMapper.utilDateToSQLTimeStamp)

  def username = column[Option[String]]("username")

  def username_fk = foreignKey("suggestion_user_fk", username, TableQuery[UserTable])(_.username)
}
