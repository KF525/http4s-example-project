package zio.client

import model.reponse.PoemResponse
import org.http4s.Status.ClientError
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.Statuses
import org.http4s.{EntityDecoder, Uri}
import zio.clock.Clock
import zio.console.Console
import zio.duration.{Duration, durationInt}
import zio.error.PoemFailure.{PoemBadResponseFailure, PoemTimedOutWithoutResponseFailure}
import zio.error.PoemFailure
import zio.interop.catz._
import zio.interop.console.cats.putStrLn
import zio.{Schedule, Task, URIO, ZIO}

class PoemClient(client: Http4sClient,
                 baseUri: Uri,
                 retryAttempts: Int = 3,
                 backoffInterval: Duration = 1.seconds,
                 timeoutPerAttempt: Duration = 1.minute) extends Http4sDsl[Task] with Statuses {

  val schedule =
    Schedule.spaced(backoffInterval) && Schedule.recurs(retryAttempts) && Schedule.recurUntil[PoemFailure](isClientError)

  def makeRequest: ZIO[Console with Any with Clock, PoemFailure, List[PoemResponse]] = {
    implicit val decoder: EntityDecoder[Task, List[PoemResponse]] = jsonOf[Task, List[PoemResponse]]

    val request: ZIO[Any with Clock, PoemFailure, List[PoemResponse]] =
      client.getRequest(baseUri).timeoutFail(PoemTimedOutWithoutResponseFailure)(timeoutPerAttempt)

    val requestWithRetries: URIO[Any with Clock, Either[PoemFailure, List[PoemResponse]]] =
      request.retry(schedule).either

    requestWithRetries.flatMap {
      case Right(response: List[PoemResponse]) => for {
        _ <- putStrLn(s"Successfully made request to $baseUri and response: $response")
      } yield response
      case Left(failure: PoemFailure) => ZIO.fail(failure)
    }
  }

  private def isClientError(poemFailure: PoemFailure): Boolean = poemFailure match {
    case PoemBadResponseFailure(_, status) if status.responseClass == ClientError => true
    case _ => false
  }
}
