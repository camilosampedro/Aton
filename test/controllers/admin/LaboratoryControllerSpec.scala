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
import model.json.LoginJson
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
  * @author P3trur0 < http://flatmap.it >
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
trait LaboratoryControllerSpec extends ControllerTest {

  // Mocked LaboratoryController dependencies
  lazy val roomService = mock[RoomService]
  lazy val messagesApi = mock[MessagesApi]
  implicit lazy val userService = mock[UserService]
  implicit lazy val environment = mock[Environment]

  // Mocked methods for testing only the controller

  //Mock laboratory service
  def mockLaboratoryService(actionState: ActionState): LaboratoryService = {
    lazy val laboratoryService = mock[LaboratoryService]
    val returnedLaboratory = if (actionState == state.ActionCompleted){
      Some(laboratory)
    } else {
      None
    }
    val alternativeState = if (actionState != state.ActionCompleted) {
      state.Failed
    } else {
      actionState
    }
    when(laboratoryService.add(any[Laboratory])) thenReturn Future.successful(alternativeState)
    when(laboratoryService.delete(any[Long])) thenReturn Future.successful(actionState)
    when(laboratoryService.getSingle(any[Long])) thenReturn Future.successful(returnedLaboratory)
    laboratoryService
  }

  // Mock user authentication
  when(userService.checkAndGet(any[String], any[String])) thenReturn Future.successful(Some(User("", "", None, Role.Administrator)))

  // Execution context is a particular exception to the mocked dependencies
  implicit lazy val executionContext: ExecutionContext = ExecutionContext.global

  // Logged in user to pass
  val loggedInUser = LoginJson("", "")
  // Laboratory with data to be tested
  val laboratory = Laboratory(id = 1l, name = "P3trur0 lab", location = Some("Italy"), administration = Some("user"))
  val command = """echo "Ciao""""



  /*"Laboratory Controller on not founding operations" should {
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
  }*/
}
