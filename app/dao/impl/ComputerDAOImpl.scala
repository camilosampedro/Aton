package dao.impl

import javax.inject.Inject

import com.google.inject.Singleton
import dao.ComputerDAO
import model.{Computer, ComputerState, ConnectedUser}
import model.table.{ComputerStateTable, ComputerTable, ConnectedUserTable}
import org.h2.jdbc.JdbcSQLException
import play.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import services.state
import services.state.{ActionCompleted, ActionState, Failed}
import slick.dbio.Effect.Read
import slick.driver.JdbcProfile
import slick.jdbc.GetResult

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Performs all Computer database actions
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param dbConfigProvider Database manager injected
  */
@Singleton
class ComputerDAOImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider) extends ComputerDAO with HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  /**
    * Table with all the computers.
    */
  implicit val computers: TableQuery[ComputerTable] = TableQuery[ComputerTable]
  implicit val computerStates: TableQuery[ComputerStateTable] = TableQuery[ComputerStateTable]
  implicit val connectedUsers: TableQuery[ConnectedUserTable] = TableQuery[ConnectedUserTable]
  implicit val tripleJoin: Query[((ComputerTable, Rep[Option[ComputerStateTable]]),
    Rep[Option[ConnectedUserTable]]), ((Computer, Option[ComputerState]), Option[ConnectedUser]), Seq] = {
    computers
    .joinLeft(computerStates)
    .on(_.ip === _.computerIp)
    .joinLeft(connectedUsers)
    .on((computerWithState,connectedUser)=>
      computerWithState._2.map(_.computerIp) === connectedUser.computerStateComputerIp
        && computerWithState._2.map(_.registeredDate) === connectedUser.computerStateRegisteredDate)
  }



  /**
    * Adds a new computer
    *
    * @param computer Computer to add
    * @return Result String message
    */
  override def add(computer: Computer): Future[ActionState] = {
    // It's done an insertion and convert the result to a String.
    db.run(computers += computer).map(res => ActionCompleted).recover {
      case ex: Exception =>
        Logger.error("An error occurred", ex)
        Failed
    }
  }

  /**
    * Gets a computer based on its IP
    *
    * @param ip Computer's IP
    * @return Some Computer found or None if its not found.
    */
  override def get(ip: String): Future[Option[Computer]] = {
    // Se realiza un select * from laboratory where id = $id
    db.run(search(ip).result.headOption)
  }

  /**
    * Deletes a computer from database
    *
    * @param ip Computer's IP
    * @return Operation result
    */
  override def delete(ip: String): Future[ActionState] = {
    db.run(search(ip).delete).map{
      case 0 => ActionCompleted
      case _ => Failed
    }
  }

  private def search(ip: String) = computers.filter(_.ip === ip)

  /**
    * Updates computer fields on the database
    * @param computer Computer to be updated, containing its ip
    * @return
    */
  override def edit(computer: Computer): Future[ActionState] = {
    db.run {
      val foundLaboratory = search(computer.ip)
      foundLaboratory.update(computer).asTry
    }.map {
      case Success(res) if res == 1 =>
        state.ActionCompleted
      case Success(_) =>
        play.Logger.info("Computer not found")
        state.NotFound
      case Failure(e: JdbcSQLException) =>
        play.Logger.error("There was an error looking for that computer", e)
        state.NotFound
      case _ => state.Failed
    }
  }

  /**
    * Lists all computers in the database.
    *
    * @return All computers found.
    */
  override def listAll: Future[Seq[(Computer, Option[ComputerState],Option[ConnectedUser])]] = db.run {
    tripleJoin
      .map(x=>(x._1._1,x._1._2,x._2))
      .result
  }

  override def listAllSimple: Future[Seq[Computer]] = db.run{
    computers.result
  }

  override def getWithStatus(ip: String): Future[Seq[(Computer, Option[ComputerState], Option[ConnectedUser])]] = db.run{
    tripleJoin
      .map(x=>
        (x._1._1,x._1._2,x._2)
      ).filter(_._1.ip === ip)
      .result
  }

  override def get(severalComputers: List[String]): Future[Seq[Computer]] = {
    implicit val getComputerResult = GetResult(c=>Computer(c.<<, c.<<, c.<<, c.<<, c.<<, c.<<))
    val ips = severalComputers.mkString(",")
    val sql = sql"""SELECT * FROM computer WHERE id IN (#$ips)""".as[Computer]
    db.run(sql)
  }

  override def getWithStatus(severalIps: List[String]): Future[Seq[(Computer, Option[ComputerState], Option[ConnectedUser])]] = db.run {
    val binded: Traversable[String] = severalIps
    tripleJoin.map(x=>
      (x._1._1,x._1._2,x._2)
    ).filter(_._1.ip inSet binded).result
  }
}

