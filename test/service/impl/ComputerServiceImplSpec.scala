package service.impl

import java.sql.Timestamp

import org.mockito.Matchers.any
import org.mockito.Mockito.when
import dao.{ComputerDAO, ComputerStateDAO}
import model.{Computer, ComputerState, ConnectedUser}
import services.SSHOrderService
import services.impl.ComputerServiceImpl
import services.state
import services.state.ActionState
import test.ServiceTest

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

/**
  * Created by camilosampedro on 6/11/16.
  */
trait ComputerServiceImplSpec extends ServiceTest {
  val executionContext = ExecutionContext.global

  implicit val username = "executor user"

  // Computers
  val computer1ip = "10.10.10.10"
  val computer1 = Computer(computer1ip, Some("My Computer 1"), "user1", "password1", Some("Description"), Some(1l))
  val computer2ip = "10.10.10.11"
  val computer2 = Computer(computer2ip, Some("My Computer 2"), "user2", "password2", Some("Description"), Some(2l))

  // Computer 1 states
  val stateDate11 = "2011-10-02 12:00:00.123456"
  val computerState11 = ComputerState(computer1ip, Timestamp.valueOf(stateDate11), model.Connected().id,
    Some("Ubuntu"), Some("12:1a:25:21:20"))
  val connectedUsers11 = Seq(
    ConnectedUser(1, "username1", computer1ip, new Timestamp(System.currentTimeMillis())),
    ConnectedUser(2, "username2", computer1ip, new Timestamp(System.currentTimeMillis()))
  )

  val stateDate12 = "2012-10-02 13:00:00.123457"
  val computerState12 = ComputerState(computer1ip, Timestamp.valueOf(stateDate12), model.NotConnected().id,
    Some("Ubuntu"), Some("12:1a:25:21:23"))
  val connectedUsers12 = Seq(
    ConnectedUser(3, "username3", computer1ip, new Timestamp(System.currentTimeMillis())),
    ConnectedUser(4, "username4", computer1ip, new Timestamp(System.currentTimeMillis()))
  )

  // Computer 2 states
  val stateDate21 = "2011-10-15 12:00:00.123456"
  val computerState21 = ComputerState(computer2ip, Timestamp.valueOf(stateDate21), model.Connected().id,
    Some("Ubuntu"), Some("12:1a:25:21:20"))
  val connectedUsers21 = Seq(
    ConnectedUser(1, "username1", computer2ip, new Timestamp(System.currentTimeMillis())),
    ConnectedUser(2, "username2", computer2ip, new Timestamp(System.currentTimeMillis()))
  )

  val stateDate22 = "2016-10-02 12:00:00.123456"
  val computerState22 = ComputerState(computer2ip, Timestamp.valueOf(stateDate22), model.NotConnected().id,
    Some("Ubuntu"), Some("12:1a:25:21:23"))
  val connectedUsers22 = Seq(
    ConnectedUser(3, "username3", computer2ip, new Timestamp(System.currentTimeMillis())),
    ConnectedUser(4, "username4", computer2ip, new Timestamp(System.currentTimeMillis()))
  )

  val computersWithStatus: Seq[(Computer, Option[ComputerState], Option[ConnectedUser])] = Seq(
    (computer1, Some(computerState11), Some(connectedUsers11.head)),
    (computer1, Some(computerState11), Some(connectedUsers11(1))),
    (computer1, Some(computerState12), Some(connectedUsers12.head)),
    (computer1, Some(computerState12), Some(connectedUsers12(1))),
    (computer2, Some(computerState21), Some(connectedUsers21.head)),
    (computer2, Some(computerState21), Some(connectedUsers21(1))),
    (computer2, Some(computerState22), Some(connectedUsers22.head)),
    (computer2, Some(computerState22), Some(connectedUsers22(1)))
  )

  def mockComputerDAO(actionState: ActionState): ComputerDAO = {
    lazy val computerDAO = mock[ComputerDAO]
    when(computerDAO.add(any[Computer])) thenReturn Future.successful(actionState)
    when(computerDAO.edit(any[Computer])) thenReturn Future.successful(actionState)
    when(computerDAO.listAll) thenReturn Future.successful(Random.shuffle(computersWithStatus))
    when(computerDAO.getWithStatus(any[List[String]])) thenReturn Future.successful(computersWithStatus.take(4))
    when(computerDAO.getWithStatus(any[String])) thenReturn Future.successful(computersWithStatus.take(4))
    when(computerDAO.get(any[List[String]])) thenReturn Future.successful(List(computer1, computer2))
    when(computerDAO.get(any[String])) thenReturn Future.successful(Some(computer1))
    when(computerDAO.delete(any[String])) thenReturn Future.successful(actionState)
    when(computerDAO.get(any[List[String]])) thenReturn Future.successful(List(computer1,computer2))
    computerDAO
  }

  def mockComputerStateDAO(actionState: ActionState): ComputerStateDAO = {
    lazy val computerStateDAO = mock[ComputerStateDAO]
    when(computerStateDAO.add(any[ComputerState])) thenReturn Future.successful(actionState)
    computerStateDAO
  }

  def mockSSHOrderService(actionState: ActionState): SSHOrderService = {
    lazy val sshOrderService = mock[SSHOrderService]
    when(sshOrderService.sendMessage(any[Computer], any[String], any[Seq[ConnectedUser]])(any[String])) thenReturn actionState
    when(sshOrderService.shutdown(any[Computer])(any[String])) thenReturn state.ActionCompleted
    //when(sshOrderService.shutdown()(any[String])) thenReturn state.ActionCompleted
    sshOrderService
  }
}
