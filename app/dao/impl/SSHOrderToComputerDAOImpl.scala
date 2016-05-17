package dao.impl

import java.sql.Timestamp
import javax.inject.Inject

import com.google.inject.Singleton
import dao.SSHOrderToComputerDAO
import model.SSHOrderToComputer
import model.table.SSHOrderToComputerTable
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
class SSHOrderToComputerDAOImpl @Inject()
(dbConfigProvider: DatabaseConfigProvider) extends SSHOrderToComputerDAO {
  /**
    * Configuración de la base de datos
    */
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._


  /**
    * Tabla con "todas las ordenes SSH", similar a select * from command
    */
  implicit val ordenesSSH = TableQuery[SSHOrderToComputerTable]

  /**
    * Adiciona una orden SSH
    *
    * @param ordenSSH command a agregar
    * @return String con el mensaje del result
    */
  override def add(ordenSSH: SSHOrderToComputer): Future[String] = {
    play.Logger.debug("Adding to database following SSH Order: " + ordenSSH)
    // Se realiza un insert y por cada insert se crea un String
    db.run(ordenesSSH += ordenSSH).map(res => {
      play.Logger.debug("Orden SSH agregada correctamente"); "success"
    }).recover {
      case ex: Exception => {
        play.Logger.error("Ocurrió un error al insertar la orden ssh al pc", ex)
        ex.getCause.getMessage
      }
    }
  }

  /**
    * Obtiene un command según el id
    *
    * @param id Identificador del command
    * @return command encontrado o None si no se encontró
    */
  override def get(id: Timestamp): Future[Option[SSHOrderToComputer]] = {
    // Se realiza un select * from command where id = $id
    db.run(search(id).result.headOption)
  }

  private def search(sshOrderDatetime: Timestamp) = ordenesSSH.filter(_.sentDateTime === sshOrderDatetime)

  /**
    * Elimina un command de la base de datos
    *
    * @param id Identificador del command
    * @return Resultado de la operación
    */
  override def delete(id: Timestamp): Future[Int] = {
    db.run(search(id).delete)
  }

  /**
    * Lista todas los ordenSSHs en la base de datos
    *
    * @return Todos los ordenSSHs
    */
  override def listAll: Future[Seq[SSHOrderToComputer]] = {
    db.run(ordenesSSH.result)
  }

  override def update(resultSSHOrder: SSHOrderToComputer): Future[Int] = {
    play.Logger.debug("Updating to the following SSH Order: " + resultSSHOrder)
    db.run(ordenesSSH.filter(_.sentDateTime === resultSSHOrder.sshOrderDatetime).update(resultSSHOrder))
  }
}
