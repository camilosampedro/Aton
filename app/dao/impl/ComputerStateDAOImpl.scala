package dao.impl

import java.sql.Timestamp
import javax.inject.Inject

import com.google.inject.Singleton
import dao.ComputerStateDAO
import model.ComputerState
import model.table.ComputerStateTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
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
class ComputerStateDAOImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider, equipoDAOImpl: ComputerDAOImpl) extends ComputerStateDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._


  /**
    * Tabla con "todos los estados", similar a select * from estado
    */
  implicit val estados = TableQuery[ComputerStateTable]

  /**
    * Adiciona un estado
    *
    * @param estado ComputerState a agregar
    * @return String con el mensaje del result
    */
  override def add(estado: ComputerState): Future[String] = {
    // Se realiza un insert y por cada insert se crea un String
    db.run(estados += estado).map(res => "ComputerState agregado correctamente").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  /**
    * Obtiene un estado según el id
    *
    * @param ip    Dirección IP del estado
    * @param fecha Fecha del estado
    * @return ComputerState encontrado o None si no se encontró
    */
  override def get(ip: String, fecha: Timestamp): Future[Option[ComputerState]] = {
    // Se realiza un select * from estado where id = $id
    db.run(search(ip, fecha).result.headOption)
  }

  /**
    * Elimina un estado de la base de datos
    *
    * @param ip    Dirección IP del estado
    * @param fecha Fecha del estado
    * @return Resultado de la operación
    */
  override def delete(ip: String, fecha: Timestamp): Future[Int] = {
    db.run(search(ip, fecha).delete)
  }

  private def search(ip: String, fecha: Timestamp) = estados.filter(a => a.computerIp === ip && a.registeredDate == fecha)

  /**
    * Lista todas los estados en la base de datos
    *
    * @return Todos los estados
    */
  override def listAll: Future[Seq[ComputerState]] = {
    db.run(estados.result)
  }
}
