package controllers.admin

import com.google.inject.Inject
import dao.{ComputerDAO, RoomDAO, UserDAO}
import model.Computer
import model.form.ComputerForm
import model.form.data.ComputerFormData
import org.scalatestplus.play.PlaySpec
import org.specs2.execute.Results
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import services.{ComputerService, SSHOrderService}
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext

/**
  * Created by camilo on 27/08/16.
  */
class ComputerControllerTest @Inject()(sSHOrderService: SSHOrderService, computerService: ComputerService, roomDAO: RoomDAO, computerDAO: ComputerDAO, val messagesApi: MessagesApi)(implicit userDAO: UserDAO, executionContext: ExecutionContext, environment: Environment) extends PlaySpec with Results {

  val controller = new ComputerController(sSHOrderService, computerService, roomDAO, computerDAO, messagesApi)

  "ComputerControllerTest" must {

    "send command" in {
    }

    "shutdown" in {

    }

    "unfreeze" in {

    }

    "blockPage" in {

    }

    "messagesApi" in {

    }

    "addForm" in {

    }

    "edit" in {

    }

    "shutdownSeveral" in {

    }

    "delete" in {

    }

    "editForm" in {

    }

    "upgrade" in {

    }

    "add" in {
      val computer = ComputerFormData("127.0.0.1", Some("Localhost"), "aton", "00000", Some(""), None)
      val computerForm = ComputerForm.form.fill(computer)
      val result = controller.add.apply(FakeRequest().withFormUrlEncodedBody().withFormUrlEncodedBody(computerForm.data.toSeq: _ *))
      val bodyText = contentAsString(result)
      bodyText mustBe "ok"

    }

    "sendMessage" in {

    }

  }
}
