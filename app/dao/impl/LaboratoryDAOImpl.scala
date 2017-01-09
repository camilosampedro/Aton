package dao.impl

import javax.inject.Inject

import com.google.inject.Singleton
import dao.{LaboratoryDAO, RoomDAO}
import model.table._
import model._
import play.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import services.state.ActionState
import services.state
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Implements DAO operations of Laboratories
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param dbConfigProvider DB manager injected.
  */
@Singleton
class LaboratoryDAOImpl @Inject()
(dbConfigProvider: DatabaseConfigProvider, roomDAO: RoomDAO) extends LaboratoryDAO {
  /**
    * Database configuration
    */
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._


  /**
    * Table with all laboratories, like select * from laboratory
    */
  implicit val laboratories = TableQuery[LaboratoryTable]
  implicit val rooms = TableQuery[RoomTable]
  implicit val computers = TableQuery[ComputerTable]
  implicit val computerStates = TableQuery[ComputerStateTable]
  implicit val connectedUsers = TableQuery[ConnectedUserTable]
  implicit val computersAndRoomsPentaJoin = {
    laboratories joinLeft rooms on {_.id === _.laboratoryId } joinLeft computers  on {(x,y)=>x._2.map(_.id)===y.roomId} joinLeft computerStates on {(x,y)=>x._2.map(_.ip)===y.computerIp} joinLeft connectedUsers on {(x,y)=>x._2.map(_.computerIp)===y.computerStateComputerIp && x._2.map(_.registeredDate) === y.computerStateRegisteredDate}
  }

  /**
    * Adds a new laboratory to database.
    *
    * @param laboratory Laboratory to add.
    * @return Result string message.
    */
  override def add(laboratory: Laboratory): Future[ActionState] = {
    Logger.debug(s"""Adding to database: $laboratory""")
    db.run(laboratories += laboratory).map(res => state.ActionCompleted).recover {
      case ex: Exception =>
        Logger.error("Ocurrió un error al adicionar en la base de datos", ex)
        state.Failed
    }
  }

  /**
    * Removes a laboratory
    *
    * @param id Laboratory's ID.
    * @return Operation result.
    */
  override def delete(id: Long): Future[ActionState] = {
    db.run(search(id).delete).map{
      case 1 => state.ActionCompleted
      case _ => state.Failed
    }
  }

  private def search(id: Long) = laboratories.filter(_.id === id)

  /**
    * List all the laboratories on the database.
    *
    * @return All the laboratories found.
    */
  override def listAll: Future[Seq[Laboratory]] = {
    db.run(laboratories.result)
  }

  /**
    * Gets the laboratory with all the rooms an computers associated.
    *
    * @param id Laboratory's ID.
    * @return Found laboratory with all its rooms and computers.
    */
  override def getWithChildren(id: Long): Future[Seq[(Laboratory, Option[Room], (Option[Computer],Option[ComputerState],Option[ConnectedUser]))]] = {
    db.run {
      computersAndRoomsPentaJoin
        .filter(_._1._1._1._1.id === id)
        .map(x => (x._1._1._1._1, x._1._1._1._2, (x._1._1._2,x._1._2,x._2)))
        .result
    }
  }

  /**
    * Gets a laboratory by its ID.
    *
    * @param id Laboratory's ID.
    * @return Some found laboratory or None if its not found.
    */
  override def get(id: Long): Future[Option[Laboratory]] = {
    // Select * from laboratory where id = $id
    db.run(search(id).result.headOption)
  }
}
