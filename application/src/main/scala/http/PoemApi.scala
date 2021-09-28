package http

import cats.effect.Sync
import cats.implicits._
import controller.PoemController
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

class PoemApi[F[_]: Sync](poemController: PoemController[F]) {

  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "line" =>
      for {
        line <- poemController.getLine
        response <- Ok(line)
      } yield response
  }
}
