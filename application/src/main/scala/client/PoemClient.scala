package client

import model.reponse.PoemResponse
import org.http4s.Status.ClientError
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.Statuses
import org.http4s.{EntityDecoder, Uri}
import zio.clock.Clock
import zio.console.Console
import zio.duration.{Duration, durationInt}
import error.PoemFailure.{PoemBadResponseFailure, PoemTimedOutWithoutResponseFailure}
import error.PoemFailure
import zio.interop.catz._
import zio.interop.console.cats.putStrLn
import zio.{Has, Schedule, Task, URIO, ZIO}

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

    val requestWithRetries: URIO[Clock, Either[PoemFailure, List[PoemResponse]]] =
      request.retry(schedule).either

    requestWithRetries.flatMap {
      case Right(response: List[PoemResponse]) => ZIO.succeed(response)
      case Left(failure: PoemFailure) => for {
        _ <- putStrLn(s"Failed to make request to $baseUri with failure: $failure")
        fail <- ZIO.fail(failure)
      } yield fail
    }
  }

  private def isClientError(poemFailure: PoemFailure): Boolean = poemFailure match {
    case PoemBadResponseFailure(_, status) if status.responseClass == ClientError => true
    case _ => false
  }
}
