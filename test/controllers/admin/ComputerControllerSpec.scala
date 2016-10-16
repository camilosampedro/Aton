package controllers.admin

import com.google.inject.Inject
import dao.{ComputerDAO, RoomDAO, UserDAO}
import model.Computer
import model.form.ComputerForm
import model.form.data.ComputerFormData
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import services.{ComputerService, RoomService, SSHOrderService, UserService}
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext
import org.scalatest.Matchers
import org.scalatest.FlatSpec

/**
 * Created by camilo on 27/08/16.
 */
class ComputerControllerSpec @Inject() (computerService: ComputerService, roomService: RoomService, val messagesApi: MessagesApi)
(implicit userService: UserService, executionContext: ExecutionContext, environment: Environment) extends FlatSpec with Matchers {

  val controller = new ComputerController(computerService, roomService, messagesApi)

  it should "send command" in {

  }

  it should "shutdown" in {

  }

  it should "unfreeze" in {

  }

  it should "blockPage" in {

  }

  it should "messagesApi" in {

  }

  it should "addForm" in {

  }

  it should "edit" in {

  }

  it should "shutdownSeveral" in {

  }

  it should "delete" in {

  }

  it should "editForm" in {

  }

  it should "upgrade" in {

  }

  it should "add" in {
    val computer = ComputerFormData("127.0.0.1", Some("Localhost"), "aton", "00000", Some(""), None)
    val computerForm = ComputerForm.form.fill(computer)
    val result = controller.add.apply(FakeRequest().withFormUrlEncodedBody().withFormUrlEncodedBody(computerForm.data.toSeq: _*))
    val bodyText = contentAsString(result)
    bodyText shouldBe "ok"
  }

  it should "sendMessage" in {

  }

}
