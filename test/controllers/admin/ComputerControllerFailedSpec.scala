package controllers.admin

import jp.t2v.lab.play2.auth.test.Helpers.AuthFakeRequest
import model.form.data._
import model.form.{BlockPageForm, ComputerForm, SSHOrderForm, SelectComputersForm}
import model.json.{ComputerJson, LoginJson}
import play.api.libs.json.Json
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
      val result = controller.edit.apply {
        FakeRequest()
          .withJsonBody(Json.parse(
            s"""
               |{
               |  "ip":"$ip",
               |  "description":"${description.getOrElse("")}",
               |  "SSHUser":"$SSHUser",
               |  "name":"${name.getOrElse("")}",
               |  "SSHPassword":"$SSHPassword",
               |  "roomID":${roomID.getOrElse(0)}
               |}
            """.stripMargin))
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 400)
    }

    "return \"Could not add that computer\" on receiving a new computer" in {
      import computer._
      val computerJson = ComputerJson(ip, name, SSHUser, SSHPassword, description, roomID)
      val result = controller.add.apply {
        FakeRequest()
          .withJsonBody(Json.toJson(computerJson))
          .withLoggedIn(controller)(loggedInUser)
      }
      assertBodyJsonMessage(result, "Could not add that computer")
    }

    "return Failed <400> status on deleting a computer" in {
      val result = controller.delete(computer.ip).apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(LoginJson("admin", "adminaton"))
      }
      assertFutureResultStatus(result, 400)
    }

    "return \"Could not deleteLaboratory that computer\" on receiving an deleting computer" in {
      val result = controller.delete(computer.ip).apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(LoginJson("admin", "adminaton"))
      }
      assertBodyJsonMessage(result, "Could not deleteLaboratory that computer")
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
      val result = controller.sendOrder.apply {
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
      val result = controller.sendOrder.apply {
        FakeRequest()
          .withJsonBody(Json.parse(
            s"""
              |{
              |  "ip": "${computer.ip}",
              |  "ssh-order": {
              |    "superUser": false,
              |    "interrupt": false,
              |    "command": ${Json.toJson(command)}
              |  }
              |}
            """.stripMargin))
          .withLoggedIn(controller)(loggedInUser)
      }
      assertBodyJsonMessage(result, "Could not send that command to that computer", emptyExtras = false)
    }
  }


}
