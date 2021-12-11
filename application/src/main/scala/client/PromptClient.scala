package client

import com.typesafe.scalalogging.StrictLogging
import error.CompoundPoemFailure
import error.CompoundPoemFailure.{NoPoemError, PoemBadResponseFailure, PoemTimedOutWithoutResponseFailure}
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
import zio.{Schedule, Task, ZIO}
import ziohelpers.ZioLoggerSyntax._

class PromptClient(client: Http4sThinClient,
                   baseUri: Uri,
                   retryAttempts: Int,
                   backoffInterval: Duration,
                   timeoutPerAttempt: Duration) extends Http4sDsl[Task] with Statuses with StrictLogging {

  val schedule: Schedule[Any, CompoundPoemFailure, ((Long, Long), CompoundPoemFailure)] =
    Schedule.spaced(backoffInterval) && Schedule.recurs(retryAttempts) && Schedule.recurUntil[CompoundPoemFailure](isClientError)

  def makeRequest: ZIO[Console with Clock, CompoundPoemFailure, PoemResponse] = {
    implicit val decoder: EntityDecoder[Task, List[PoemResponse]] = jsonOf[Task, List[PoemResponse]]

    val request: ZIO[Clock, CompoundPoemFailure, List[PoemResponse]] =
      client.getRequest(baseUri).timeoutFail(PoemTimedOutWithoutResponseFailure)(timeoutPerAttempt)

    request.retry(schedule).foldM(err => for {
      _ <- logger.errorZ(s"Failed to make request to $baseUri with failure: $err")
      f <- ZIO.fail(err)
    } yield f, {
      case Nil => ZIO.fail(NoPoemError)
      case h :: _ => ZIO.succeed(h)
    })
  }

  //TODO: Do we want to retry NoPoemError
  private def isClientError(poemFailure: CompoundPoemFailure): Boolean = poemFailure match {
    case PoemBadResponseFailure(_, status) if status.responseClass == ClientError => true
    case _ => false
  }
}
