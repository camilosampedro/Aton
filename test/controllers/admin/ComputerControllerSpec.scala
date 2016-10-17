package controllers.admin

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import jp.t2v.lab.play2.auth.test.Helpers.AuthFakeRequest
import model.Computer
import model.Role
import model.User
import model.form.{ BlockPageForm, ComputerForm, SSHOrderForm, SelectComputersForm }
import model.form.data._
import org.mockito.Mock
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.inject.Injector
import play.test.WithApplication
import services.state.ActionState
import services.{ ComputerService, RoomService, UserService, state }
import test.ControllerTest

/**
 * Created by camilo on 27/08/16.
 */
class ComputerControllerSpec extends ControllerTest {

  // Define the fake application to execute for the tests
  def application = new WithApplication

  // Before all the tests, start the fake Play application
  override def beforeAll() {
    application.startPlay()
  }

  // After the tests execution, shut down the fake application
  override def afterAll() {
    application.stopPlay()
  }

  // Mocked ComputerController dependencies
  lazy val roomService = mock[RoomService]
  lazy val messagesApi = mock[MessagesApi]
  implicit lazy val userService = mock[UserService]
  implicit lazy val environment = mock[Environment]

  // Mocked methods for testing only the controller
  def mockComputerService(actionState: ActionState): ComputerService = {
    lazy val computerService = mock[ComputerService]
    val alternativeState = if(actionState!=state.ActionCompleted){
      state.Failed
    } else {
      actionState
    }
    when(computerService.add(any[String], any[Option[String]], any[String], any[String], any[Option[String]],
      any[Option[Long]])) thenReturn Future.successful(alternativeState)
    when(computerService.delete(any[String])) thenReturn Future.successful(actionState)
    when(computerService.blockPage(any[String], any[String])(any[String])) thenReturn Future.successful(actionState)
    when(computerService.edit(any[Computer])) thenReturn Future.successful(actionState)
    when(computerService.shutdown(any[String])(any[String])) thenReturn Future.successful(actionState)
    when(computerService.shutdown(any[List[String]])(any[String])) thenReturn Future.successful(actionState)
    when(computerService.upgrade(any[String])(any[String])) thenReturn Future.successful(actionState)
    when(computerService.unfreeze(any[String])(any[String])) thenReturn Future.successful(actionState)
    when(computerService.sendCommand(any[String], any[Boolean], any[String])(any[String])) thenReturn Future.successful(actionState)
    computerService
  }

  // Mock user authentication
  when(userService.checkAndGet(any[String], any[String])) thenReturn Future.successful(Some(User("", "", None, Role.Administrator)))

  // Execution context is a particular exception to the mocked dependencies
  implicit lazy val executionContext: ExecutionContext = ExecutionContext.global

  // Logged in user to pass
  val loggedInUser = LoginFormData("", "")
  // Computer with data to be tested
  val computer = Computer(ip = "127.0.0.1", name = Some("Localhost"), SSHUser = "user", SSHPassword = "password",
    description = Some(""), roomID = Some(1))
  val command = "echo \"Hola\""

  def createTestForResult(whatShouldBeReturned: String, actionState: ActionState, stateCode: Int) = {
    val computerService = mockComputerService(actionState)
    // Controller to be tested, with the dependencies
    lazy val controller = new ComputerController(computerService, roomService, messagesApi)(userService, executionContext, environment)

    s"return $whatShouldBeReturned status on receiving an edited computer" in {
      import computer._
      val computerData = ComputerFormData(ip, name, SSHUser, SSHPassword, description, roomID)
      val computerForm = ComputerForm.form.fill(computerData)
      val result = controller.edit.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(computerForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, stateCode)
    }

    s"return $whatShouldBeReturned status on deleting a computer" in {
      val result = controller.delete(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(LoginFormData("admin", "adminaton"))
      }
      assertFutureResultStatus(result, stateCode)
    }

    s"return $whatShouldBeReturned status on blocking a page on a single computer" in {
      val result = controller.blockPage(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(BlockPageForm.form.fill(BlockPageFormData("www.example.com")).data.toSeq: _*)
      }
      assertFutureResultStatus(result, stateCode)
    }

    s"return $whatShouldBeReturned status on shutting down a computer" in {
      val result = controller.shutdown(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, stateCode)
    }

    s"return $whatShouldBeReturned status on shutting down several computer" in {
      val computersData = SelectComputersFormData(Seq(computer.ip).toList)
      val computersForm = SelectComputersForm.form.fill(computersData)
      val result = controller.shutdownSeveral().apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(computersForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, stateCode)
    }

    s"return $whatShouldBeReturned status on upgrading a computer" in {
      val result = controller.upgrade(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, stateCode)
    }

    s"return $whatShouldBeReturned status on unfreezing a computer" in {
      val result = controller.unfreeze(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, stateCode)
    }

    s"return $whatShouldBeReturned status on sending a command to a computer" in {
      val sshOrderData = SSHOrderFormData(superUser = false, command)
      val sshOrderForm = SSHOrderForm.form.fill(sshOrderData)
      val result = controller.sendCommand(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(sshOrderForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, stateCode)
    }

    controller
  }

  "Computer Controller on successful operations" should {
    val controller = createTestForResult("Ok", state.ActionCompleted, 200)
    "return Ok status on adding a new computer" in {
      import computer._
      val computerData = ComputerFormData(ip, name, SSHUser, SSHPassword, description, roomID)
      val computerForm = ComputerForm.form.fill(computerData)
      val result = controller.add.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(computerForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 200)
    }
  }

  "Computer Controller on not founding operations" should {
    val controller = createTestForResult("NotFound", state.NotFound, 404)
    "return BadRequest status on adding a new computer" in {
      import computer._
      val computerData = ComputerFormData(ip, name, SSHUser, SSHPassword, description, roomID)
      val computerForm = ComputerForm.form.fill(computerData)
      val result = controller.add.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(computerForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 400)
    }
  }
}
