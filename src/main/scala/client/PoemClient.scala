package client

import cats.effect.Sync
import model.Poem
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.{EntityDecoder, Method, Request, Uri}

class PoemClient[F[_]: Sync](client: Client[F], baseUri: Uri) {

  /**
   * Http4s client expect submits a request and decodes response on success.
   * Status code returned on failure.
   * Underlying HTTP connection is closed at completion of decoding.
   */
  def getPoem: F[List[Poem]] = {
    implicit val decoder: EntityDecoder[F, List[Poem]] = jsonOf[F, List[Poem]]
    client.expect[List[Poem]](Request[F](Method.GET, baseUri / "random" / "1"))
  }

}