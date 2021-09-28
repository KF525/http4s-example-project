package http

import cats.effect.Sync
import cats.implicits._
import controller.CompoundPoemController
import model.request.CompoundPoemRequest
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}

class CompoundPoemApi[F[_]: Sync](controller: CompoundPoemController[F]) {

  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  /**
   * curl -d '{"initialLine":"initial line", "initialAuthor":"author1", "inspiredLine":"inspired line", "inspiredAuthor":"author2" }' -X POST localhost:8027/compound
   */
  val routes: HttpRoutes[F] = HttpRoutes.of {
    case rawRequest@POST -> Root / "compound" =>
      for {
        request <- rawRequest.as[CompoundPoemRequest]
        poem <- controller.save(request)
        response <- Created(poem)
      } yield response
    case GET -> Root / "compound" =>
      for {
        poems <- controller.view
        response <- Ok(poems)
      } yield response
  }
}
