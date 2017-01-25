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
import model.form.{BlockPageForm, ComputerForm, SSHOrderForm, SelectComputersForm}
import model.form.data._
import model.json.{ComputerJson, LoginJson}
import org.mockito.Mock
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.inject.Injector
import play.test.WithApplication
import services.state.ActionState
import services.{ComputerService, RoomService, UserService, state}
import test.ControllerTest

import scala.language.postfixOps

/**
  * Computer specifications on successful operations
  * @see controllers.admin.ComputerControllerSpec for mocked dependencies and other methods used here
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class ComputerControllerSuccessfulSpec extends ComputerControllerSpec {

  val computerService = mockComputerService(state.ActionCompleted)
  /**
    * Controller to be tested, with the dependencies
    */
  lazy val controller = new ComputerController(computerService, roomService, messagesApi)(userService, executionContext, environment)

  "Computer Controller on successful operations" should {
    "return Ok <200> status on receiving an edited computer" in {
      import computer._
      val computerData = ComputerFormData(ip, name, SSHUser, SSHPassword, description, roomID)
      val computerForm = ComputerForm.form.fill(computerData)
      val result = controller.update.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(computerForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 200)
    }

    "return \"Computer edited successfully\" response message JSON on receiving an edited computer" in {
      import computer._
      val computerData = ComputerFormData(ip, name, SSHUser, SSHPassword, description, roomID)
      val computerForm = ComputerForm.form.fill(computerData)
      val result = controller.update.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(computerForm.data.toSeq: _*)
      }
      assertBodyJsonMessage(result, "Computer edited successfully")
    }

    "return Ok <200> status on deleting a computer" in {
      val result = controller.delete(computer.ip).apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(LoginJson("admin", "adminaton"))
      }
      assertFutureResultStatus(result, 200)
    }

    "return \"Computer deleted successfully\" response message JSON on deleting a computer" in {
      val result = controller.delete(computer.ip).apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(LoginJson("admin", "adminaton"))
      }
      assertBodyJsonMessage(result, "Computer deleted successfully")
    }

    "return Ok <200> status on blocking a page on a single computer" in {
      val result = controller.blockPage.apply {
        FakeRequest()
          .withJsonBody(blockPageJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 200)
    }

    "return \"Page blocked successfully on the computer\" response message JSON on blocking a page on a single computer" in {
      val result = controller.blockPage.apply {
        FakeRequest()
          .withJsonBody(blockPageJson)
          .withLoggedIn(controller)(loggedInUser)

      }
      assertBodyJsonMessage(result, "Page blocked successfully on the computer")
    }

    "return Ok <200> status on shutting down a computer" in {
      val result = controller.shutdown.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 200)
    }

    "return \"Computer shutdown successfully\" response message JSON on shutting down a computer" in {
      val result = controller.shutdown.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertBodyJsonMessage(result, "Computer shutdown successfully")
    }

    "return Ok <200> status on upgrading a computer" in {
      val result = controller.upgrade.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 200)
    }

    "return \"Computer upgraded successfully\" response message JSON on upgrading a computer" in {
      val result = controller.upgrade.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertBodyJsonMessage(result, "Computer upgraded successfully")
    }

    "return Ok <200> status on unfreezing a computer" in {
      val result = controller.unfreeze.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 200)
    }

    "return \"Computer unfreezed successfully\" response message JSON on unfreezing a computer" in {
      val result = controller.unfreeze.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertBodyJsonMessage(result, "Computer unfreezed successfully")
    }

    "return Ok <200> status on sending a command to a computer" in {
      val sshOrderData = SSHOrderFormData(superUser = false, command)
      val sshOrderForm = SSHOrderForm.form.fill(sshOrderData)
      val result = controller.sendCommand.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(sshOrderForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 200)
    }

    "return \"Order sent successfully\" response message JSON on sending a command to a computer" in {
      val sshOrderData = SSHOrderFormData(superUser = false, command)
      val sshOrderForm = SSHOrderForm.form.fill(sshOrderData)
      val result = controller.sendCommand.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(sshOrderForm.data.toSeq: _*)
      }
      assertBodyJsonMessage(result,"Order sent successfully")
    }

    "return Ok <200> status on adding a new computer" in {
      import computer._
      val computerData = ComputerJson(ip, name, SSHUser, SSHPassword, description, roomID)
      val json = Json.toJson(computerData)
      val result = controller.add.apply {
        FakeRequest()
          .withJsonBody(json)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 200)
    }

    "return \"Computer added successfully\" response message JSON on adding a new computer" in {
      import computer._
      val computerData = ComputerJson(ip, name, SSHUser, SSHPassword, description, roomID)
      val json = Json.toJson(computerData)
      val result = controller.add.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withJsonBody(json)
      }
      assertBodyJsonMessage(result, "Computer added successfully")
    }
  }


}
