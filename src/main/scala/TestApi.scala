import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes

class TestApi[F[_]: Sync](client: TestClient[F]) {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "health" =>  Ok("healthy")
    case GET -> Root / "covid" / state =>
      for {
        text <- client.getSomething(state)
        response <- Ok(text)
      } yield response
    case GET -> Root / "great" / "white" / "whale" =>
      for {
        text <- client.getMobyDick
        response <- Ok(text)
      } yield response
  }
}
