package http

import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import zio.Task
import zio.interop.catz._
import controller.PoemController

class PoemApi(poemController: PoemController) extends Http4sDsl[Task] {

  val routes: HttpRoutes[Task] =
    HttpRoutes.of {
      case GET -> Root / "line" =>
      for {
        line <- poemController.getLine
        response <- Ok(line)
      } yield response
  }
}
