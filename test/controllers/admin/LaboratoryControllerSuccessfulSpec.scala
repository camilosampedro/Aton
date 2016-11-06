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
  * Created by camilosampedro on 5/11/16.
  */
class LaboratoryControllerSuccessfulSpec extends LaboratoryControllerSpec {
  val labService = mockLaboratoryService(state.ActionCompleted)
  // Controller to be tested, with the dependencies
  lazy val controller = new LaboratoryController(labService, messagesApi)(userService, executionContext, environment)

  "Laboratory Controller on successful operations" should {
    "return Ok <200> status on receiving an edited laboratory" in {
      import laboratory._
      val laboratoryData = LaboratoryFormData(name, location, administration)
      val laboratoryForm = LaboratoryForm.form.fill(laboratoryData)
      val result = controller.edit(laboratory.id).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(laboratoryForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 200)
    }

    "return Ok <200> status on deleting a laboratory" in {
      val result = controller.delete(laboratory.id).apply {
        FakeRequest()
          .withLoggedIn(controller)(LoginFormData("admin", "adminaton"))
      }
      assertFutureResultStatus(result, 200)
    }

    "return Ok <200> status on adding a new laboratory" in {
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

    "return Ok <200> status when listing all laboratories" in pending
    "return laboratory list json when listing all laboratories" in pending
  }
}
