package dao.impl

import javax.inject.Inject

import com.google.inject.Singleton
import dao.ComputerDAO
import model.Computer
import model.table.ComputerTable
import play.Logger
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
class ComputerDAOImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider) extends ComputerDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /**
    * Tabla con "todos los equipos", similar a select * from laboratory
    */
  implicit val equipos = TableQuery[ComputerTable]

  /**
    * Adiciona un laboratory
    *
    * @param equipo Computer a agregar
    * @return String con el mensaje del result
    */
  override def add(equipo: Computer): Future[String] = {
    // Se realiza un insert y por cada insert se crea un String
    db.run(equipos += equipo).map(res => "Computer agregado correctamente").recover {
      case ex: Exception => {
        Logger.error("Ocurrió un error agregando un equipo", ex)
        ex.getMessage
      }
    }
  }

  /**
    * Obtiene un laboratory según el id
    *
    * @param ip Dirección IP del laboratory
    * @return Computer encontrado o None si no se encontró
    */
  override def get(ip: String): Future[Option[Computer]] = {
    // Se realiza un select * from laboratory where id = $id
    db.run(search(ip).result.headOption)
  }

  /**
    * Elimina un laboratory de la base de datos
    *
    * @param ip Dirección IP del laboratory
    * @return Resultado de la operación
    */
  override def delete(ip: String): Future[Int] = {
    db.run(search(ip).delete)
  }

  private def search(ip: String) = equipos.filter(_.ip === ip)

  override def edit(computer: Computer): Future[Int] = db.run {
    equipos.filter(_.ip === computer.ip).update(computer)
  }

  /**
    * Lista todos los equipos en la base de datos
    *
    * @return Todos los equipos
    */
  override def listAll: Future[Seq[Computer]] = db.run(equipos.result)


}

