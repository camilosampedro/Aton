package controllers.admin

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.mockito.Matchers.any
import org.mockito.Mockito.when

import com.google.inject.ImplementedBy
import com.google.inject.Inject

import jp.t2v.lab.play2.auth.test.Helpers.AuthFakeRequest
import model.Laboratory
import model.Role
import model.User
import model.form.LaboratoryForm
import model.form.data.LaboratoryFormData
import model.form.data.LoginFormData
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.test.WithApplication
import services.LaboratoryService
import services.RoomService
import services.UserService
import services.impl.LaboratoryServiceImpl
import services.state
import services.state.ActionState
import test.ControllerTest

/**
 * @author P3trur0, http://flatmap.it
 */
class LaboratoryControllerSpec extends ControllerTest {

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

  // Mocked LaboratoryController dependencies
  lazy val roomService = mock[RoomService]
  lazy val messagesApi = mock[MessagesApi]
  implicit lazy val userService = mock[UserService]
  implicit lazy val environment = mock[Environment]

  // Mocked methods for testing only the controller

  //Mock laboratory service
  def mockLaboratoryService(actionState: ActionState): LaboratoryService = {
    lazy val laboratoryService = mock[LaboratoryService]
    
     val alternativeState = if(actionState!=state.ActionCompleted){
      state.Failed
    } else {
      actionState
    }
    
    when(laboratoryService.add(any[Laboratory])) thenReturn Future.successful(alternativeState)
    laboratoryService
  }
  
  // Mock user authentication
  when(userService.checkAndGet(any[String], any[String])) thenReturn Future.successful(Some(User("", "", None, Role.Administrator)))

  // Execution context is a particular exception to the mocked dependencies
  implicit lazy val executionContext: ExecutionContext = ExecutionContext.global

  // Logged in user to pass
  val loggedInUser = LoginFormData("", "")
  // Laboratory with data to be tested
  val laboratory = Laboratory(id=1l, name = "P3trur0 lab", location=Some("Italy"), administration=Some("user"))
  val command = s"""echo "Ciao""""

  def createTestForResult(whatShouldBeReturned: String, actionState: ActionState, stateCode: Int) = {
    val labService = mockLaboratoryService(actionState)
    // Controller to be tested, with the dependencies
    lazy val controller = new LaboratoryController(labService, messagesApi)(userService, executionContext, environment)

    s"return $whatShouldBeReturned status on receiving an edited laboratory" in {
      import laboratory._
      val laboratoryData = LaboratoryFormData(name, location, administration)
      val laboratoryForm = LaboratoryForm.form.fill(laboratoryData)
      val result = controller.edit(laboratory.id).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(laboratoryForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, stateCode)
    }

    s"return $whatShouldBeReturned status on deleting a laboratory" in {
      val result = controller.delete(laboratory.id).apply {
        FakeRequest()
          .withLoggedIn(controller)(LoginFormData("admin", "adminaton"))
      }
      assertFutureResultStatus(result, stateCode)
    }

    controller
  }

  "Laboratory Controller on successful operations" should {
    val controller = createTestForResult("Ok", state.ActionCompleted, 200)
    "return Ok status on adding a new laboratory" in {
      import laboratory._
      val laboratoryData = LaboratoryFormData(name, location, administration)
      val laboratoryForm = LaboratoryForm.form.fill(laboratoryData)
      val result = controller.add.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(laboratoryForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 200)
    }
  }

  "Laboratory Controller on not founding operations" should {
    val controller = createTestForResult("NotFound", state.NotFound, 404)
    "return BadRequest status on adding a new laboratory" in {
      import laboratory._
      val laboratoryData = LaboratoryFormData(name, location, administration)
      val laboratoryForm = LaboratoryForm.form.fill(laboratoryData)
      val result = controller.add.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(laboratoryForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 400)
    }
  }

  "Laboratory Controller on failing operations" should {
    val controller = createTestForResult("BadRequest", state.Failed, 400)
    "return BadRequest status on adding a new laboratory" in {
      import laboratory._
      val laboratoryData = LaboratoryFormData(name, location, administration)
      val laboratoryForm = LaboratoryForm.form.fill(laboratoryData)
      val result = controller.add.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(laboratoryForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 400)
    }
  }
}
