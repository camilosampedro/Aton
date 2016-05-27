package dao

import com.google.inject.ImplementedBy
import dao.impl.LaboratoryDAOImpl
import model._

import scala.concurrent.Future

/**
  * Controla las acciones sobre la base de datos.
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[LaboratoryDAOImpl])
trait LaboratoryDAO {

  /**
    * Obtiene el laboratory con todos las rooms y PC asociadas
    *
    * @param id
    * @return
    */
  def getWithChildren(id: Long): Future[Seq[(Laboratory, Option[Room], (Option[Computer],Option[ComputerState],Option[ConnectedUser]))]]


  /**
    * Adiciona un laboratory
    *
    * @param laboratorio Laboratory a agregar
    * @return String con el mensaje del result
    */
  def add(laboratorio: Laboratory): Future[String]

  /**
    * Obtiene un laboratory según el id
    *
    * @param id Identificador del laboratory
    * @return Laboratory encontrado o None si no se encontró
    */
  def get(id: Long): Future[Option[Laboratory]]

  /**
    * Elimina un laboratory de la base de datos
    *
    * @param id Identificador del laboratory
    * @return Resultado de la operación
    */
  def delete(id: Long): Future[Int]

  /**
    * Lista todos los laboratorios en la base de datos
    *
    * @return Todos los laboratorios
    */
  def listAll: Future[Seq[Laboratory]]
}
