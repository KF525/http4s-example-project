package zio.error

import org.http4s.Response
import zio.Task
import zio.interop.catz._

object HttpErrorHandler {
  def handleNon200(response: Response[Task]) = {
    val status = response.status
    val maybeString = response.attemptAs[String]

    for {
      body <- maybeString.getOrElse("Unparseable body")
      exception = PoemBadResponseFailure(body, status)
    } yield exception
  }
}
