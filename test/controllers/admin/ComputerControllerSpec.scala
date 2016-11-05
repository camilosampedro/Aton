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
 * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
 */
trait ComputerControllerSpec extends ControllerTest {

  // Mocked ComputerController dependencies
  lazy val roomService = mock[RoomService]
  lazy val messagesApi = mock[MessagesApi]
  implicit lazy val userService = mock[UserService]
  implicit lazy val environment = mock[Environment]

  /**
    * Mocked computer service methods for testing only the controller
    * @param actionState Action state to be returned when methods being executed
    * @return Mocked computer service
    */
  def mockComputerService(actionState: ActionState): ComputerService = {
    // Mock the computer service
    lazy val computerService = mock[ComputerService]

    // This state will be used for methods that don't have other states that ActionCompleted and Failed
    val alternativeState = if(actionState!=state.ActionCompleted){
      state.Failed
    } else {
      actionState
    }

    // For example, add will not have more than ActionCompleted and Failed states.
    when(computerService.add(any[String], any[Option[String]], any[String], any[String], any[Option[String]],
      any[Option[Long]])) thenReturn Future.successful(alternativeState)
    // delete will do have more than those two states
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

  /**
    * Mock user authentication
    */
  when(userService.checkAndGet(any[String], any[String])) thenReturn Future.successful(Some(User("", "", None, Role.Administrator)))

  /**
    * Execution context is a particular exception to the mocked dependencies
    */
  implicit lazy val executionContext: ExecutionContext = ExecutionContext.global

  /**
    * Logged in user to pass
    */
  val loggedInUser = LoginFormData("", "")
  /**
    * Computer with data to be tested
    */
  val computer = Computer(ip = "127.0.0.1", name = Some("Localhost"), SSHUser = "user", SSHPassword = "password",
    description = Some(""), roomID = Some(1))
  /**
    * Sample command
    */
  val command = "echo \"Hola\""

  /*"Computer Controller on not founding operations" should {
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

  "Computer Controller on failing operations" should {
    val controller = createTestForResult("BadRequest", state.Failed, 400)
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
  }*/
}
