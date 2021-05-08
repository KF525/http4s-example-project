package http

import cats.effect.Sync
import cats.implicits._
import client.PoemClient
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

class PoemApi[F[_]: Sync](client: PoemClient[F]) {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "poem" => //TODO: Get rid of the list
      for {
        poem <- client.getPoem
        response <- Ok(poem)
      } yield response
  }
}
