package http

import cats.effect.Sync
import cats.implicits._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import store.CompoundPoemStore
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

class CompoundPoemApi[F[_]: Sync](repository: CompoundPoemStore[F]) {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "health" =>  Ok("healthy")
    case GET -> Root / "compound" / "create" => Ok("this is where I will create a compound poem!")
    case GET -> Root / "doobie" => {
      repository.test.map{ e => e.fold(_ => Ok("fail"), t => Ok(t))}.flatten
    }
  }
}
