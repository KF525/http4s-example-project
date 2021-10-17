package http

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.Task
import zio.interop.catz._

class Routes() extends Http4sDsl[Task] {

  val routes: HttpRoutes[Task] = HttpRoutes.of {
    case GET -> Root / "health" / "ping" => Ok("pong")
  }
}
