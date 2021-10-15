package zio.http

import model.reponse.PoemLineResponse
import org.http4s.{HttpRoutes, Response}
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import zio.{Task, ZIO}
import zio.controller.PoemController
import zio.interop.catz._

class PoemApi(poemController: PoemController) extends Http4sDsl[Task] {

  val routes =
    HttpRoutes.of {
      case GET -> Root / "line" =>
      for {
        line <- poemController.getLine
        response <- Ok(line)
      } yield response
  }
}
