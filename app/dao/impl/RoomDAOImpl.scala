package dao.impl

import javax.inject.Inject

import com.google.inject.Singleton
import dao.RoomDAO
import model.Room
import model.table.RoomTable
import org.h2.jdbc.JdbcSQLException
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import services.state.ActionState
import services.state
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Se encarga de implementar las acciones sobre la base de datos
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param dbConfigProvider Inyección del gestor de la base de datos
  */
@Singleton
class RoomDAOImpl @Inject()
(dbConfigProvider: DatabaseConfigProvider) extends RoomDAO {
  /**
    * Configuración de la base de datos
    */
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._


  /**
    * Tabla con "todos los rooms", similar a select * from roomPanel
    */
  implicit val rooms = TableQuery[RoomTable]

  /**
    * Adiciona una roomPanel
    *
    * @param room Room a agregar
    * @return String con el mensaje del result
    */
  override def add(room: Room): Future[ActionState] = {
    db.run(rooms += room).map(_ => state.ActionCompleted).recover {
      case ex: Exception =>
        play.Logger.error("Room adding exception",ex)
        state.Failed
    }
  }

  /**
    * Obtiene una roomPanel según el id
    *
    * @param id Identificador del roomPanel
    * @return Room encontrado o None si no se encontró
    */
  override def get(id: Long): Future[Option[Room]] = {
    // Se realiza un select * from roomPanel where id = $id
    db.run(search(id).result.headOption)
  }

  private def search(id: Long) = rooms.filter(_.id === id)

  /**
    * Elimina una roomPanel de la base de datos
    *
    * @param id Identificador de la roomPanel
    * @return Resultado de la operación
    */
  override def delete(id: Long): Future[ActionState] = {
    db.run(search(id).delete).map{
      case 0 => state.ActionCompleted
      case _ => state.Failed
    }
  }

  /**
    * Lista todas los rooms en la base de datos
    *
    * @return Todas las rooms
    */
  override def listAll: Future[Seq[Room]] = {
    db.run(rooms.result)
  }

  override def getByLaboratory(id: Long): Future[Seq[Room]] = {
    db.run(rooms.filter(_.laboratoryId === id).result)
  }

  override def update(room: Room): Future[ActionState] = {
    db.run {
      val foundLaboratory = search(room.id)
      foundLaboratory.update(room).asTry
    }.map{
      case Success(res) if res == 1 =>
        play.Logger.info(s"updated with result: $res")
        state.ActionCompleted
      case Success(_) =>
        play.Logger.info("Room not found")
        state.NotFound
      case Failure(e: JdbcSQLException) =>
        play.Logger.error("There was an error looking for that room",e)
        state.NotFound
      case _ => state.Failed
    }
  }
}

