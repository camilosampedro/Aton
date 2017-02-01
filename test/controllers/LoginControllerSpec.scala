package controllers

import model.json.LoginJson
import model.{Computer, Role, User}
import org.mockito.Matchers._
import org.mockito.Mockito._
import play.api.Environment
import play.api.i18n.MessagesApi
import services.state.ActionState
import services.{UserService, state}
import test.ControllerTest

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
trait LoginControllerSpec extends ControllerTest {
  // Mocked ComputerController dependencies
  lazy val messagesApi = mock[MessagesApi]
  implicit lazy val environment = mock[Environment]
  val userToBeUsed = User("user","password",Some("User Name"),Role.NormalUser)
  val userToBeUsed2 = User("user2","password2",Some("User Name Two"),Role.Administrator)
  val userList = List(userToBeUsed,userToBeUsed2)

  /**
    * Execution context is a particular exception to the mocked dependencies
    */
  implicit lazy val executionContext: ExecutionContext = ExecutionContext.global

  /**
    * Mocked computer service methods for testing only the controller
    * @param actionState Action state to be returned when methods being executed
    * @return Mocked computer service
    */
  def mockUserService(actionState: ActionState): UserService = {
    // Mock the computer service
    lazy val userService = mock[UserService]

    val user = if(actionState == state.ActionCompleted){
      Some(userToBeUsed)
    } else {
      None
    }

    // This state will be used for methods that don't have other states that ActionCompleted and Failed
    val alternativeState = if(actionState!=state.ActionCompleted){
      state.Failed
    } else {
      actionState
    }

    when(userService.checkAndGet(any[LoginJson])) thenReturn(Future.successful(user))
    when(userService.checkAndGet(any[String],any[String])) thenReturn(Future.successful(user))
    when(userService.add(any[User])) thenReturn(Future.successful(actionState))
    when(userService.get(any[String])) thenReturn(Future.successful(user))
    when(userService.listAll) thenReturn(Future.successful(userList))

    userService
  }
}
