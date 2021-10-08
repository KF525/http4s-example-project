package zio.http

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.Task
import zio.controller.PoemController
import zio.interop.catz._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

class PoemApi(poemController: PoemController) extends Http4sDsl[Task] {

  val routes: HttpRoutes[Task] = HttpRoutes.of {
    case GET -> Root / "line" => Ok("")
//      for {
//        line <- poemController.getLine
//        response <- Ok(line)
//      } yield response
  }
}
