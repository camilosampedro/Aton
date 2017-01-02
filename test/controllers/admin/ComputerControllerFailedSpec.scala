package controllers.admin

import jp.t2v.lab.play2.auth.test.Helpers.AuthFakeRequest
import model.form.data._
import model.form.{BlockPageForm, ComputerForm, SSHOrderForm, SelectComputersForm}
import model.json.LoginJson
import play.api.test.FakeRequest
import services.state

/**
  * Computer specifications on successful operations
  * @see controllers.admin.ComputerControllerSpec for mocked dependencies and other methods used here
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class ComputerControllerFailedSpec extends ComputerControllerSpec {


  val computerService = mockComputerService(state.Failed)
  /**
    * Controller to be tested, with the dependencies
    */
  lazy val controller = new ComputerController(computerService, roomService, messagesApi)(userService, executionContext, environment)

  "Computer Controller on failed operations" should {
    "return Failed <400> status on receiving an edited computer" in {
      import computer._
      val computerData = ComputerFormData(ip, name, SSHUser, SSHPassword, description, roomID)
      val computerForm = ComputerForm.form.fill(computerData)
      val result = controller.edit.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(computerForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 400)
    }

    "return \"Could not add that computer\" on receiving an edited computer" in {
      import computer._
      val computerData = ComputerFormData(ip, name, SSHUser, SSHPassword, description, roomID)
      val computerForm = ComputerForm.form.fill(computerData)
      val result = controller.edit.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(computerForm.data.toSeq: _*)
      }
      assertBodyJsonMessage(result, "Could not edit that computer")
    }

    "return Failed <400> status on deleting a computer" in {
      val result = controller.delete(computer.ip).apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(LoginJson("admin", "adminaton"))
      }
      assertFutureResultStatus(result, 400)
    }

    "return \"Could not delete that computer\" on receiving an deleting computer" in {
      val result = controller.delete(computer.ip).apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(LoginJson("admin", "adminaton"))
      }
      assertBodyJsonMessage(result, "Could not delete that computer")
    }

    "return Failed <400> status on blocking a page on a single computer" in {
      val result = controller.blockPage.apply {
        FakeRequest()
          .withJsonBody(blockPageJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 400)
    }

    "return \"Could not block that page\" on blocking a page on a single computer" in {
      val result = controller.blockPage.apply {
        FakeRequest()
          .withJsonBody(blockPageJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertBodyJsonMessage(result, "Could not block that page", emptyExtras = false)
    }

    "return Failed <400> status on shutting down a computer" in {
      val result = controller.shutdown.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 400)
    }

    "return \"Could not shutdown that computer\" on shutting down a computer" in {
      val result = controller.shutdown.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertBodyJsonMessage(result, "Could not shutdown that computer")
    }

    "return Failed <400> status on upgrading a computer" in {
      val result = controller.upgrade.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 400)
    }

    "return \"Could not upgrade that computer\" on upgrading a computer" in {
      val result = controller.upgrade.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertBodyJsonMessage(result, "Could not upgrade that computer", emptyExtras = true)
    }

    "return Failed <400> status on unfreezing a computer" in {
      val result = controller.unfreeze.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 400)
    }

    "return \"Could not unfreeze that computer\" on unfreezing a computer" in {
      val result = controller.unfreeze.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertBodyJsonMessage(result, "Could not unfreeze that computer", emptyExtras = true)
    }

    "return Failed <400> status on sending a command to a computer" in {
      val sshOrderData = SSHOrderFormData(superUser = false, command)
      val sshOrderForm = SSHOrderForm.form.fill(sshOrderData)
      val result = controller.sendCommand.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(sshOrderForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 400)
    }

    "return \"Could not send that command to that computer\" on sending a command to a computer" in {
      val sshOrderData = SSHOrderFormData(superUser = false, command)
      val sshOrderForm = SSHOrderForm.form.fill(sshOrderData)
      val result = controller.sendCommand.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(sshOrderForm.data.toSeq: _*)
      }
      assertBodyJsonMessage(result, "Could not send that command to that computer", emptyExtras = false)
    }
  }


}
