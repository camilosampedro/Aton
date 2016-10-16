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

import com.google.inject.ImplementedBy
import com.google.inject.Inject

import dao.ComputerDAO
import dao.RoomDAO
import dao.UserDAO
import dao.impl.ComputerDAOImpl
import dao.impl.RoomDAOImpl
import dao.impl.UserDAOImpl
import jp.t2v.lab.play2.auth.test.Helpers.AuthFakeRequest
import model.Computer
import model.Role
import model.User
import model.form.ComputerForm
import model.form.data.ComputerFormData
import model.form.data.LoginFormData
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.test.WithApplication
import services.ComputerService
import services.SSHOrderService
import services.impl.ComputerServiceImpl
import services.impl.SSHOrderServiceImpl

/**
  * Created by camilo on 27/08/16.
  */
class ComputerControllerSpec extends PlaySpec with MockitoSugar with BeforeAndAfterAll {
  
//define the fake application to execute for the tests
  def application = new WithApplication
 
  //before all the tests, start the fake Play application
  override def beforeAll() {
     application.startPlay()
  }
  
  //after the tests execution, shut down the fake application
  override def afterAll() {
     application.stopPlay()
  }
  
  
  lazy val sSHOrderService = mock[SSHOrderService]
  lazy val computerService = mock[ComputerService]
  //when(computerService.add(any[Computer])) thenReturn Future.successful("Computer added")
  lazy val roomDAO = mock[RoomDAO]
  // lazy val computerDAO = inject[ComputerDAO]
  lazy val computerDAO = mock[ComputerDAO]
  // when(computerDAO.add(any[Computer])) thenReturn Future.successful("")
  lazy val messagesApi = mock[MessagesApi]
  implicit lazy val userDAO = mock[UserDAO]
  //when(userDAO.get(any[LoginFormData])) thenReturn Future.successful(Some(User("","",None,Role.Administrator)))
  //implicit lazy val executionContext = inject[ExecutionContext]//ExecutionContext.global//app.injector.instanceOf[ExecutionContext]
  implicit lazy val environment = mock[Environment]
  when(computerService.add(any[Computer])(any[ExecutionContext])) thenReturn Future.successful("Computer added")
  when(computerDAO.add(any[Computer])) thenReturn Future.successful("")
  when(userDAO.get(any[LoginFormData])) thenReturn Future.successful(Some(User("", "", None, Role.Administrator)))

  
  
  "ComputerController" should {
    "add a new computer" in {
      implicit lazy val executionContext: ExecutionContext = ExecutionContext.global
      lazy val controller = new ComputerController(sSHOrderService, computerService, roomDAO, computerDAO, messagesApi)
      val computer = ComputerFormData("127.0.0.1", Some("Localhost"), "aton", "00000", Some(""), None)
      val computerForm = ComputerForm.form.fill(computer)
      val result = controller.add.apply {
        FakeRequest()
          .withLoggedIn(controller)(LoginFormData("admin", "adminaton"))
          .withFormUrlEncodedBody(computerForm.data.toSeq: _*)
      }
     
      val bodyText = Await.result(result, 20.seconds)
      
      assert(bodyText.header.status === 303)  //this should actually return 200
    }
  }
}
