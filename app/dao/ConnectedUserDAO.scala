package dao

import java.sql.Timestamp

import com.google.inject.ImplementedBy
import dao.impl.ConnectedUserDAOImpl
import model.ConnectedUser

import scala.concurrent.Future

/**
  * Controla las acciones sobre la base de datos.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[ConnectedUserDAOImpl])
trait ConnectedUserDAO {

  /**
    * Adiciona una sesion
    *
    * @param user ComputerSession a agregar
    * @return String con el mensaje del result
    */
  def add(user: ConnectedUser): Future[String]

  /**
    * Obtiene una sesion según el id
    *
    * @param ip    Dirección IP de la sesion
    * @param fecha Fecha de la sesion
    * @return ComputerSession encontrado o None si no se encontró
    */
  def get(ip: String, fecha: Timestamp): Future[Seq[ConnectedUser]]

  /**
    * Elimina una sesion de la base de datos
    *
    * @return Resultado de la operación
    */
  def delete(id: Int): Future[Int]
}
