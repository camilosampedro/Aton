package dao.impl

import java.sql.Timestamp
import javax.inject.Inject

import com.google.inject.Singleton
import dao.SessionDAO
import model.ComputerSession
import model.table.SessionTable
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
class SessionDAOImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider, equipoDAOImpl: ComputerDAOImpl) extends SessionDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._


  /**
    * Tabla con "todos los sesions", similar a select * from sesion
    */
  implicit val sesions = TableQuery[SessionTable]

  /**
    * Adiciona un sesion
    *
    * @param sesion ComputerSession a agregar
    * @return String con el mensaje del result
    */
  override def add(sesion: ComputerSession): Future[String] = {
    // Se realiza un insert y por cada insert se crea un String
    db.run(sesions += sesion).map(res => "ComputerSession agregado correctamente").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  /**
    * Obtiene un sesion según el id
    *
    * @param ip    Dirección IP del sesion
    * @param fecha Fecha del sesion
    * @return ComputerSession encontrado o None si no se encontró
    */
  override def get(ip: String, fecha: Timestamp): Future[Option[ComputerSession]] = {
    // Se realiza un select * from sesion where id = $id
    db.run(search(ip, fecha).result.headOption)
  }

  /**
    * Elimina un sesion de la base de datos
    *
    * @param ip    Dirección IP del sesion
    * @param fecha Fecha del sesion
    * @return Resultado de la operación
    */
  override def delete(ip: String, fecha: Timestamp): Future[Int] = {
    db.run(search(ip, fecha).delete)
  }

  private def search(ip: String, fecha: Timestamp) = sesions.filter(a => a.computerIp === ip && a.connectionTime == fecha)

  /**
    * Lista todas los sesions en la base de datos
    *
    * @return Todos los sesions
    */
  override def listAll: Future[Seq[ComputerSession]] = {
    db.run(sesions.result)
  }
}
