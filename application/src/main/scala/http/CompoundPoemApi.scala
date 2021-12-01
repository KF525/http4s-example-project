package http

import controller.{CompoundPoemController, PromptController}
import model.request.CompoundPoemRequest
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.Task
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import zio.interop.catz._
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder

case class CompoundPoemApi(controller: CompoundPoemController) extends Http4sDsl[Task] {

  val routes: HttpRoutes[Task] = HttpRoutes.of {
    case rawRequest@POST -> Root / "compound" =>
      for {
        request <- rawRequest.as[CompoundPoemRequest]
        poem <- controller.save(request)
        response <- Created(poem)
      } yield response
    case GET -> Root / "compound" =>
      for {
        poems <- controller.view
        response <- Ok(poems)
      } yield response
  }
}
