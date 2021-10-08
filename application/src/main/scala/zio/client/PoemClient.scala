package zio.client

import model.reponse.PoemResponse
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.client.middleware.Logger
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.Statuses
import org.http4s.{EntityDecoder, Request, Response, Uri}
import zio.clock.Clock
import zio.console.Console
import zio.duration.{Duration, durationInt}
import zio.error.{HttpErrorHandler, PoemFailure, PoemTimedOutWithoutResponseFailure}
import zio.interop.catz._
import zio.interop.console.cats.putStrLn
import zio.{Schedule, Task, ZIO}

class PoemClient(client: Client[Task],
                 baseUri: Uri,
                 retryAttempts: Int = 3,
                 backoffInterval: Duration = 1.seconds,
                 maxTotalDuration: Duration = 1.minute) extends Http4sDsl[Task] with Statuses {

  val schedule: Schedule[Any, Any, ((Long, Long), Duration)] = Schedule.spaced(backoffInterval) &&
    Schedule.recurs(retryAttempts) && Schedule.upTo(maxTotalDuration)

  def makeRequest: ZIO[Console with Any with Clock, PoemFailure, List[PoemResponse]] = {
    implicit val decoder: EntityDecoder[Task, List[PoemResponse]] = jsonOf[Task, List[PoemResponse]]
    val request: Task[List[PoemResponse]] =
      Logger(logBody = true, logHeaders = true)(client).expectOr[List[PoemResponse]](
      Request[Task](method = GET, uri = baseUri / "random" / "1")
    )(HttpErrorHandler.handleNon200)

    val requestWithRetries =
      request.retry(schedule).timeoutFail(PoemTimedOutWithoutResponseFailure)(maxTotalDuration).either

    requestWithRetries.flatMap {
      case Right(response: List[PoemResponse]) => for {
        _ <- putStrLn(s"Successfully made request to $baseUri and response: $response")
      } yield response
      case Left(failure: PoemFailure) => ZIO.fail(failure)
    }
  }
}
