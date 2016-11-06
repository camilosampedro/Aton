package test

import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Result
import play.api.test.PlaySpecification
import play.test.WithApplication

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by camilosampedro on 16/10/16.
  */
trait ControllerTest extends PlaySpec with MockitoSugar with BeforeAndAfterAll {

  /**
    * Defines the fake application to execute for the tests
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

  def assertFutureResultStatus(future: Future[Result], status: Int) = {
    val result: Result = Await.result(future,20 seconds)
    assert(result.header.status == status)
  }
}
