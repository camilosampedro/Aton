package controllers.admin

import jp.t2v.lab.play2.auth.test.Helpers.AuthFakeRequest
import model.form.LaboratoryForm
import model.form.data.{LaboratoryFormData, LoginFormData}
import model.json.LaboratoryJson
import play.api.libs.json.Json
import play.api.test.FakeRequest
import services.{LaboratoryService, state}

/**
  * Created by camilosampedro on 5/11/16.
  */
class LaboratoryControllerFailedSpec extends LaboratoryControllerSpec {
  val labService: LaboratoryService = mockLaboratoryService(state.Failed)
  // Controller to be tested, with the dependencies
  lazy val controller = new LaboratoryController(labService, messagesApi)(userService, executionContext, environment)

  "Laboratory Controller on failing operations" should {
    "return Failed <400> status on adding a new laboratory" in {
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

    "return \"Could not add that laboratory\" response message JSON on adding a new laboratory" in {
      import laboratory._
      val laboratoryData = LaboratoryJson(name, location, administration)
      val result = controller.add.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withJsonBody(Json.toJson(laboratoryData))
      }
      assertBodyJsonMessage(result, "Could not add that laboratory")
    }
  }
}
