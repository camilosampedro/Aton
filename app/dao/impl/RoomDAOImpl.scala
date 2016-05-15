package dao.impl

import javax.inject.Inject

import com.google.inject.Singleton
import dao.RoomDAO
import model.Room
import model.table.RoomTable
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile

import scala.concurrent.Future

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
  implicit val salas = TableQuery[RoomTable]

  /**
    * Adiciona una roomPanel
    *
    * @param sala Room a agregar
    * @return String con el mensaje del result
    */
  override def add(sala: Room): Future[String] = {
    // Se realiza un insert y por cada insert se crea un String
    db.run(salas += sala).map(res => "Room agregada correctamente").recover {
      case ex: Exception => ex.getCause.getMessage
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

  private def search(id: Long) = salas.filter(_.id === id)

  /**
    * Elimina una roomPanel de la base de datos
    *
    * @param id Identificador de la roomPanel
    * @return Resultado de la operación
    */
  override def delete(id: Long): Future[Int] = {
    db.run(search(id).delete)
  }

  /**
    * Lista todas los rooms en la base de datos
    *
    * @return Todas las rooms
    */
  override def listAll: Future[Seq[Room]] = {
    db.run(salas.result)
  }

  override def getByLaboratory(id: Long): Future[Seq[Room]] = {
    db.run(salas.filter(_.laboratoryId === id).result)
  }
}

