package test

import org.scalatest.{BeforeAndAfterAll, _}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Result
import play.test.WithApplication
import services.state
import services.state.ActionState
import org.scalatest.Matchers._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by camilosampedro on 6/11/16.
  */
trait ServiceTest extends PlaySpec with MockitoSugar with BeforeAndAfterAll {
  /**
    * Defines the fake application to execute for the tests
    *
    * @return
    */
  lazy val application = new WithApplication

  /**
    * Before all the tests, start the fake Play application
    */
  override def beforeAll() {
    application.startPlay()
  }

  /**
    * After the tests execution, shut down the fake application
    */
  override def afterAll() {
    application.stopPlay()
  }

  def assertState(future: Future[ActionState], actionState: ActionState) = {
    val result = Await.result(future, 20 seconds)
    assert(result === actionState)
  }

  def assertStateWithResult(future: Future[ActionState], actionState: ActionState, expectedResult: String, expectedExitCode: Int) = {
    val result = Await.result(future, 20 seconds)
    result  match {
      case state.OrderCompleted(output, exitCode) =>
        output shouldBe expectedResult
        exitCode shouldBe expectedExitCode
      case anythingElse =>
        fail(s"Unexpected state, $anythingElse is not an $actionState")
    }
  }

  def waitFor[T](future: Future[T]): T = {
    Await.result(future, 20 seconds)
  }
}
