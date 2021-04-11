import cats.effect.Sync
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl
import cats.syntax.functor._, cats.syntax.flatMap._

class TestApi[F[_]: Sync](client: TestClient[F]) {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "health" => Ok("healthy")
    case GET -> Root / "tada" => Ok("magic")
    case GET -> Root / "text" / id => {
      println("here")
      val x: F[Response[F]] = for {
        text <- client.getSomething(id)
        response <- Ok(text)
      } yield response
      x
    }
  }
}
