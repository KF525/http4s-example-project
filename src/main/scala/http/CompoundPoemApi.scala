package http

import cats.effect.Sync
import cats.implicits._
import client.PoemClient
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import repository.CompoundPoemRepository

class CompoundPoemApi[F[_]: Sync](client: PoemClient[F], repository: CompoundPoemRepository[F]) {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "health" =>  Ok("healthy")
    case GET -> Root / "poem" => //TODO: Get rid of the list
      for {
        poem <- client.getPoem
        response <- Ok(poem)
      } yield response
    case GET -> Root / "doobie" => {
      for {
        tuple <- repository.test
        response <- Ok(tuple)
      } yield response
    }
  }
}
