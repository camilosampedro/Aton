package controllers

import play.api.libs.json.Json
import play.api.test.FakeRequest
import services.state

/**
  * Created by camilosampedro on 5/11/16.
  */
class LoginControllerSuccessfulSpec extends LoginControllerSpec {
  val userService = mockUserService(state.ActionCompleted)
  /**
    * Controller to be tested, with the dependencies
    */
  val controller = new LoginController(userService, messagesApi)(executionContext,environment)

  "Login Controller on successful operations" should {
    "return Ok <200> status on requesting the Login Form" in {
      val result = controller.login.apply{
        FakeRequest().withJsonBody(Json.parse(
          s"""
            |{
            | "username": "${userToBeUsed.username}",
            | "password": "${userToBeUsed.password}"
            |}
          """.stripMargin))
      }
      assertFutureResultStatus(result, 200)
    }
  }
}
