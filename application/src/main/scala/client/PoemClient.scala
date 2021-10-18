package client

import error.PoemFailure
import error.PoemFailure.{PoemBadResponseFailure, PoemTimedOutWithoutResponseFailure}
import model.reponse.PoemResponse
import org.http4s.Status.ClientError
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.Statuses
import org.http4s.{EntityDecoder, Uri}
import zio.clock.Clock
import zio.console.Console
import zio.duration.Duration
import zio.interop.catz._
import zio.interop.console.cats.putStrLn
import zio.{Schedule, Task, ZIO}

class PoemClient(client: Http4sClient,
                 baseUri: Uri,
                 retryAttempts: Int,
                 backoffInterval: Duration,
                 timeoutPerAttempt: Duration) extends Http4sDsl[Task] with Statuses {

  val schedule: Schedule[Any, PoemFailure, ((Long, Long), PoemFailure)] =
    Schedule.spaced(backoffInterval) && Schedule.recurs(retryAttempts) && Schedule.recurUntil[PoemFailure](isClientError)

  def makeRequest: ZIO[Console with Clock, PoemFailure, List[PoemResponse]] = {
    implicit val decoder: EntityDecoder[Task, List[PoemResponse]] = jsonOf[Task, List[PoemResponse]]

    val request: ZIO[Clock, PoemFailure, List[PoemResponse]] =
      client.getRequest(baseUri).timeoutFail(PoemTimedOutWithoutResponseFailure)(timeoutPerAttempt)

    request.retry(schedule).foldM(err => for {
      _ <-  putStrLn(s"Failed to make request to $baseUri with failure: $err")
      f <- ZIO.fail(err)
    } yield f, response => ZIO.succeed(response))
  }

  private def isClientError(poemFailure: PoemFailure): Boolean = poemFailure match {
    case PoemBadResponseFailure(_, status) if status.responseClass == ClientError => true
    case _ => false
  }
}
