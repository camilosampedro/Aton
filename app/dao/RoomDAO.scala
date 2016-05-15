package dao

import com.google.inject.ImplementedBy
import dao.impl.RoomDAOImpl
import model.Room
import play.api.mvc.Result

import scala.concurrent.Future

/**
  * Controla las acciones sobre la base de datos.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[RoomDAOImpl])
trait RoomDAO {
  def getByLaboratory(id: Long): Future[Seq[Room]]


  /**
    * Adiciona una roomPanel
    *
    * @param sala Room a agregar
    * @return String con el mensaje del result
    */
  def add(sala: Room): Future[String]

  /**
    * Obtiene una roomPanel según el id
    *
    * @param id Identificador del roomPanel
    * @return Room encontrado o None si no se encontró
    */
  def get(id: Long): Future[Option[Room]]

  /**
    * Elimina una roomPanel de la base de datos
    *
    * @param id Identificador del roomPanel
    * @return Resultado de la operación
    */
  def delete(id: Long): Future[Int]

  /**
    * Lista todas las rooms en la base de datos
    *
    * @return Todas las rooms
    */
  def listAll: Future[Seq[Room]]
}
