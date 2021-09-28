package client

import cats.effect.Sync
import model.reponse.PoemResponse
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.{EntityDecoder, Method, Request, Uri}

class PoemClient[F[_]: Sync](client: Client[F], baseUri: Uri) {

  def getPoem: F[List[PoemResponse]] = {
    implicit val decoder: EntityDecoder[F, List[PoemResponse]] = jsonOf[F, List[PoemResponse]]
    client.expect[List[PoemResponse]](Request[F](Method.GET, baseUri / "random" / "1"))
  }
}