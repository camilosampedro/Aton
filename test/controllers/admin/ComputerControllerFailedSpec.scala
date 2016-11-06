package controllers.admin

import jp.t2v.lab.play2.auth.test.Helpers.AuthFakeRequest
import model.form.data._
import model.form.{BlockPageForm, ComputerForm, SSHOrderForm, SelectComputersForm}
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
    s"return Failed <400> status on receiving an edited computer" in {
      import computer._
      val computerData = ComputerFormData(ip, name, SSHUser, SSHPassword, description, roomID)
      val computerForm = ComputerForm.form.fill(computerData)
      val result = controller.edit.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(computerForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 400)
    }

    s"return Failed <400> status on deleting a computer" in {
      val result = controller.delete(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(LoginFormData("admin", "adminaton"))
      }
      assertFutureResultStatus(result, 400)
    }

    s"return Failed <400> status on blocking a page on a single computer" in {
      val result = controller.blockPage(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(BlockPageForm.form.fill(BlockPageFormData("www.example.com")).data.toSeq: _*)
      }
      assertFutureResultStatus(result, 400)
    }

    s"return Failed <400> status on shutting down a computer" in {
      val result = controller.shutdown(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 400)
    }

    s"return Failed <400> status on shutting down several computer" in {
      val computersData = SelectComputersFormData(Seq(computer.ip).toList)
      val computersForm = SelectComputersForm.form.fill(computersData)
      val result = controller.shutdownSeveral().apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(computersForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 400)
    }

    s"return Failed <400> status on upgrading a computer" in {
      val result = controller.upgrade(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 400)
    }

    s"return Failed <400> status on unfreezing a computer" in {
      val result = controller.unfreeze(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 400)
    }

    s"return Failed <400> status on sending a command to a computer" in {
      val sshOrderData = SSHOrderFormData(superUser = false, command)
      val sshOrderForm = SSHOrderForm.form.fill(sshOrderData)
      val result = controller.sendCommand(computer.ip).apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(sshOrderForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 400)
    }
  }


}
