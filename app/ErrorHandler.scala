import com.google.inject.Inject
import play.api.{Application, Play}
import play.api.http.{DefaultHttpErrorHandler, HttpErrorHandler, Status}
import play.api.mvc.{RequestHeader, Result, Results}
import views.html.index

import scala.concurrent.Future

/**
  * Created by camilosampedro on 9/01/17.
  */
class ErrorHandler @Inject()(errorHandler: DefaultHttpErrorHandler) extends HttpErrorHandler {
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    statusCode match {
      case clientError if statusCode > 400 && statusCode < 500 => Future.successful(Results.NotFound(index("Aton")))
      case _ => Future.successful(Results.ServiceUnavailable("Unexpected error happened"))
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    errorHandler.onServerError(request,exception)
  }
}
