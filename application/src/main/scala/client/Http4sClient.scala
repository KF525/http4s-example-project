package client

import model.reponse.PoemResponse
import org.http4s.Method.GET
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.impl.Statuses
import org.http4s.{Request, Response, Uri}
import error.CompoundPoemFailure
import error.CompoundPoemFailure.{PoemBadResponseFailure, PoemGeneralFailure}
import zio.interop.catz._
import zio.{IO, Task}

class Http4sClient(httpClient: Client[Task]) extends Http4sClientDsl[Task] with Statuses {
  def getRequest(baseUri: Uri): IO[CompoundPoemFailure, List[PoemResponse]] =
    httpClient.expectOr[List[PoemResponse]](
      Request[Task](method = GET, uri = baseUri / "random" / "1")
    )(handleNon200).mapError{
      case f: CompoundPoemFailure => f
      case e => PoemGeneralFailure(e.getMessage)
  }

  private def handleNon200(response: Response[Task]): Task[Throwable] = {
    val status = response.status
    val maybeString = response.attemptAs[String]

    for {
      body <- maybeString.getOrElse("Unparseable body")
      exception = PoemBadResponseFailure(body, status)
    } yield exception
  }
}
