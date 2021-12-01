package http

import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import zio.Task
import zio.interop.catz._
import controller.PromptController

class PromptApi(poemController: PromptController) extends Http4sDsl[Task] {

  val routes: HttpRoutes[Task] =
    HttpRoutes.of {
      case GET -> Root / "prompt" =>
      for {
        line <- poemController.getPrompt
        response <- Ok(line)
      } yield response
  }
}
