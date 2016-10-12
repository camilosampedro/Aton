package controllers.admin

import test.Inject
import dao.{ComputerDAO, RoomDAO, UserDAO}
import org.scalatestplus.play.OneAppPerSuite
import model.{Computer, Role, User}
import model.form.ComputerForm
import model.form.data.{ComputerFormData, LoginFormData}
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.test.{FakeApplication, FakeRequest}
import services.{ComputerService, SSHOrderService}
import play.api.test.Helpers._
import jp.t2v.lab.play2.auth.test.Helpers._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.global
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Results
import play.test.WithApplication
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by camilo on 27/08/16.
  */
class ComputerControllerSpec extends PlaySpec with Results /*with OneAppPerSuite*/ with MockitoSugar with ScalaFutures with IntegrationPatience {
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


  "ComputerController" should {
    "add a new computer" in new WithApplication with Inject {
      val injector = app.injector()
      implicit lazy val executionContext = inject[ExecutionContext]
      when(computerService.add(any[Computer])) thenReturn Future.successful("Computer added")
      when(computerDAO.add(any[Computer])) thenReturn Future.successful("")
      when(userDAO.get(any[LoginFormData])) thenReturn Future.successful(Some(User("","",None,Role.Administrator)))
      lazy val controller = new ComputerController(sSHOrderService, computerService, roomDAO, computerDAO, messagesApi)
      val computer = ComputerFormData("127.0.0.1", Some("Localhost"), "aton", "00000", Some(""), None)
      val computerForm = ComputerForm.form.fill(computer)
      val result = controller.add.apply {
        FakeRequest()
          .withLoggedIn(controller)(LoginFormData("admin", "adminaton"))
          .withFormUrlEncodedBody(computerForm.data.toSeq: _*)
      }
      val bodyText = contentAsString(result)
      bodyText mustBe ""
    }
  }

}
