import cats.effect.Sync
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl
import cats.syntax.functor._
import cats.syntax.flatMap._
import monix.eval.Task

class TestApi(client: TestClient) {
  val dsl: Http4sDsl[Task] = new Http4sDsl[Task] {}
  import dsl._

  val routes: HttpRoutes[Task] = HttpRoutes.of {
    case GET -> Root / "health" => Ok("healthy")
    case GET -> Root / "tada" => Ok("magic")
    case GET -> Root / "text" / id => {
      println("here")
      val x: Task[Response[Task]] = for {
        text <- client.getSomething(id)
        response <- Ok(text)
      } yield response
      x
    }
  }
}
