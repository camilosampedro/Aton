package controllers.admin

import jp.t2v.lab.play2.auth.test.Helpers.AuthFakeRequest
import model.form.LaboratoryForm
import model.form.data.LaboratoryFormData
import model.json.LoginJson
import play.api.test.FakeRequest
import services.state

/**
  * Created by camilosampedro on 5/11/16.
  */
class LaboratoryControllerNotFoundSpec extends LaboratoryControllerSpec {
  val labService = mockLaboratoryService(state.NotFound)
  // Controller to be tested, with the dependencies
  lazy val controller = new LaboratoryController(labService, messagesApi)(userService, executionContext, environment)

  "Laboratory Controller on not founding operations" should {
    "return Not Found <404> status on receiving an edited laboratory" in {
      import laboratory._
      val laboratoryData = LaboratoryFormData(name, location, administration)
      val laboratoryForm = LaboratoryForm.form.fill(laboratoryData)
      val result = controller.update.apply {
        FakeRequest()
          .withLoggedIn(controller)(loggedInUser)
          .withFormUrlEncodedBody(laboratoryForm.data.toSeq: _*)
      }
      assertFutureResultStatus(result, 404)
    }

    "return Not Found <404> status on deleting a laboratory" in {
      val result = controller.delete(laboratory.id).apply {
        FakeRequest()
          .withLoggedIn(controller)(LoginJson("admin", "adminaton"))
      }
      assertFutureResultStatus(result, 404)
    }
  }
}
