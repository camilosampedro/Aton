package dao

import com.google.inject.ImplementedBy
import dao.impl.ComputerDAOImpl
import model.{Computer, ComputerState, ConnectedUser}

import scala.concurrent.Future

/**
  * Controla las acciones sobre la base de datos.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[ComputerDAOImpl])
trait ComputerDAO {

  /**
    * Adiciona un inicio
    *
    * @param equipo Computer a agregar
    * @return String con el mensaje del result
    */
  def add(equipo: Computer): Future[String]

  /**
    * Obtiene un inicio según la ip
    *
    * @param ip Dirección IP del inicio
    * @return Computer encontrado o None si no se encontró
    */
  def get(ip: String): Future[Option[Computer]]

  /**
    * Elimina un inicio de la base de datos
    *
    * @param ip Dirección IP del inicio
    * @return Resultado de la operación
    */
  def delete(ip: String): Future[Int]

  /**
    * Lista todas los computers en la base de datos
    *
    * @return Todos los computers
    */
  def listAll: Future[Seq[(Computer,Option[ComputerState],Option[ConnectedUser])]]

  def edit(computer: Computer): Future[Int]
}

