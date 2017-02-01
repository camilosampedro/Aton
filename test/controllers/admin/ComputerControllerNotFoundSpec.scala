package controllers.admin

import jp.t2v.lab.play2.auth.test.Helpers.AuthFakeRequest
import model.form.data._
import model.form.{BlockPageForm, ComputerForm, SSHOrderForm, SelectComputersForm}
import model.json.LoginJson
import play.api.libs.json.Json
import play.api.test.FakeRequest
import services.state

/**
  * Computer specifications on successful operations
  * @see controllers.admin.ComputerControllerSpec for mocked dependencies and other methods used here
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class ComputerControllerNotFoundSpec extends ComputerControllerSpec {


  val computerService = mockComputerService(state.NotFound)
  /**
    * Controller to be tested, with the dependencies
    */
  lazy val controller = new ComputerController(computerService, roomService, messagesApi)(userService, executionContext, environment)

  "Computer Controller on not found operations" should {
    s"return Not Found <404> status on receiving an edited computer" in {
      import computer._
      play.Logger.debug(Json.toJson(computer).toString())
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
      assertFutureResultStatus(result, 404)
    }

    s"return Not Found <404> status on deleting a computer" in {
      val result = controller.delete(computer.ip).apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(LoginJson("admin", "adminaton"))
      }
      assertFutureResultStatus(result, 404)
    }

    s"return Not Found <404> status on blocking a page on a single computer" in {
      val result = controller.blockPage.apply {
        FakeRequest()
          .withJsonBody(blockPageJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 404)
    }

    s"return Not Found <404> status on shutting down a computer" in {
      val result = controller.shutdown.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 404)
    }

    s"return Not Found <404> status on upgrading a computer" in {
      val result = controller.upgrade.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 404)
    }

    s"return Not Found <404> status on unfreezing a computer" in {
      val result = controller.unfreeze.apply {
        FakeRequest()
          .withJsonBody(ipJson)
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 404)
    }

    s"return Not Found <404> status on sending a command to a computer" in {
      val sshOrderData = SSHOrderFormData(superUser = false, command)
      val sshOrderForm = SSHOrderForm.form.fill(sshOrderData)
      val result = controller.sendOrder.apply {
        FakeRequest()
          .withJsonBody(Json.parse(
            s"""
              |{
              |  "ip": "${computer.ip}",
              |  "ssh-order": {
              |    "command": ${Json.toJson(command)},
              |    "superUser": false,
              |    "interrupt": false
              |  }
              |}
            """.stripMargin))
          .withLoggedIn(controller)(loggedInUser)
      }
      assertFutureResultStatus(result, 404)
    }
  }


}
