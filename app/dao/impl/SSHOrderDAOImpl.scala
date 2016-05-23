package dao.impl

import java.sql.Timestamp
import javax.inject.Inject

import com.google.inject.Singleton
import dao.SSHOrderDAO
import model.SSHOrder
import model.table.SSHOrderTable
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
class SSHOrderDAOImpl @Inject()
(dbConfigProvider: DatabaseConfigProvider) extends SSHOrderDAO {
  /**
    * Configuración de la base de datos
    */
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._


  /**
    * Tabla con "todas las ordenes SSH", similar a select * from command
    */
  implicit val ordenesSSH = TableQuery[SSHOrderTable]

  /**
    * Adiciona una orden SSH
    *
    * @param ordenSSH command a agregar
    * @return String con el mensaje del result
    */
  override def add(ordenSSH: SSHOrder): Future[Option[Long]] = {
    // Se realiza un insert y por cada insert se crea un String
    //play.Logger.debug(s"""Adding the following ssh order: ${ordenSSH}""")
    db.run(ordenesSSH returning ordenesSSH.map(_.id) into ((sshOrder, id) => sshOrder.copy(id = id)) += ordenSSH).map(res => Some(res.id)).recover {
      case ex: Exception => play.Logger.error("There was an error adding the ssh order: " + ordenSSH, ex)
        None
    }
  }

  /**
    * Obtiene un command según el id
    *
    * @param id Identificador del command
    * @return command encontrado o None si no se encontró
    */
  override def get(id: Long): Future[Option[SSHOrder]] = {
    // Se realiza un select * from command where id = $id
    db.run(search(id).result.headOption)
  }

  private def search(id: Long) = ordenesSSH.filter(_.id === id)

  /**
    * Elimina un command de la base de datos
    *
    * @param id Identificador del command
    * @return Resultado de la operación
    */
  override def delete(id: Long): Future[Int] = {
    db.run(search(id).delete)
  }

  /**
    * Lista todas los ordenSSHs en la base de datos
    *
    * @return Todos los ordenSSHs
    */
  override def listAll: Future[Seq[SSHOrder]] = {
    db.run(ordenesSSH.result)
  }
}
