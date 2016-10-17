package test

import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Result

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by camilosampedro on 16/10/16.
  */
trait ControllerTest extends PlaySpec with MockitoSugar with BeforeAndAfterAll {
  def assertFutureResultStatus(future: Future[Result], status: Int) = {
    val result = Await.result(future,20 seconds)
    assert(result.header.status === status)
  }
}
