package dao

import java.sql.Timestamp

import com.google.inject.ImplementedBy
import dao.impl.ComputerStateDAOImpl
import model.ComputerState

import scala.concurrent.Future

/**
  * Controla las acciones sobre la base de datos.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[ComputerStateDAOImpl])
trait ComputerStateDAO {

  /**
    * Adiciona un estado
    *
    * @param estado ComputerState a agregar
    * @return String con el mensaje del result
    */
  def add(estado: ComputerState): Future[String]

  /**
    * Obtiene un estado según el identificador compuesto
    *
    * @param ip    Dirección IP del estado
    * @param fecha Fecha del estado
    * @return ComputerState encontrado o None si no se encontró
    */
  def get(ip: String, fecha: Timestamp): Future[Option[ComputerState]]

  /**
    * Elimina un estado de la base de datos
    *
    * @param ip    Dirección IP del estado
    * @param fecha Fecha del estado
    * @return Resultado de la operación
    */
  def delete(ip: String, fecha: Timestamp): Future[Int]

  /**
    * Lista todos los estados en la base de datos
    *
    * @return Todos los estados
    */
  def listAll: Future[Seq[ComputerState]]
}
