package http

import controller.PromptController
import model.User
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import zio.Task
import zio.interop.catz._

case class PromptApi(poemController: PromptController) extends Http4sDsl[Task] {

  val routes: AuthedRoutes[User, Task] =
    AuthedRoutes.of {
      case GET -> Root / "prompt" as user =>
      for {
        line <- poemController.getPrompt
        response <- Ok(line)F
      } yield response
  }
}
