package model.table

import java.sql.Timestamp

import model.{Suggestion, User}
import slick.driver.H2Driver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

/**
  * Suggestion table map with Slick
  *
  *
  * @param tag Table tag
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class SuggestionTable(tag: Tag) extends Table[Suggestion](tag, "SUGGESTION") {

  // All tables need the * method with the type that it was created the table with.
  override def * : ProvenShape[Suggestion] =
  (id, suggestionText, registeredDate, username) <> (Suggestion.tupled, Suggestion.unapply)

  // Primary key
  def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  // Other columns/attributes
  def suggestionText: Rep[String] = column[String]("SUGGESTION_TEXT")

  def registeredDate: Rep[Timestamp] = column[Timestamp]("REGISTERED_DATE") //(DateMapper.utilDateToSQLTimeStamp)

  def username: Rep[Option[String]] = column[Option[String]]("USERNAME")

  def username_fk: ForeignKeyQuery[UserTable, User] = foreignKey("SUGGESTION_USER_FK", username, TableQuery[UserTable])(_.username.?)
}
