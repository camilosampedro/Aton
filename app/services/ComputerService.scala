package services

import com.google.inject.ImplementedBy
import model.{Computer, ComputerState, ConnectedUser}
import play.api.mvc.Result
import services.impl.ComputerServiceImpl
import services.state.ActionState

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
@ImplementedBy(classOf[ComputerServiceImpl])
trait ComputerService {
  def blockPage(ip: List[String], page: String)(implicit username: String): Future[ActionState]

  def sendCommand(ip: List[String], superUser: Boolean, interrupt: Boolean, command: String)(implicit username: String): Future[ActionState]

  def unfreeze(ip: List[String])(implicit username: String): Future[ActionState]

  def upgrade(ips: List[String])(implicit username: String): Future[ActionState]

  def shutdown(ips: List[String])(implicit username: String): Future[ActionState]

  def getWithStatus(ip: String): Future[Seq[(Computer, Option[ComputerState], Option[ConnectedUser])]]

  def delete(ip: String): Future[ActionState]

  def get(ip: String): Future[Option[(Computer, Option[(ComputerState,Seq[ConnectedUser])])]]
  def getSingle(ip: String): Future[Option[Computer]]
  def getSeveral(ips: List[String]): Future[Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]]
  def getSeveralSingle(ips: List[String]): Future[Seq[Computer]]

  def installAPackage(ip: String, programs: String)(implicit username: String): Future[ActionState]
  def sendMessage(ip: List[String], message: String)(implicit username: String): Future[ActionState]

  def listAllSimple: Future[Seq[Computer]]
  def listAll: Future[Seq[(Computer, Option[(ComputerState, Seq[ConnectedUser])])]]

  def add(computer: Computer): Future[ActionState]
  def add(ip: String, name: Option[String], sSHUser: String, sSHPassword: String, description: Option[String], roomID: Option[Long]): Future[ActionState]

  def edit(computer: Computer): Future[ActionState]
}
