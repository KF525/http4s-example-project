package http

import cats.effect.Sync
import cats.implicits._
import controller.CompoundPoemController
import model.CompoundPoem
import model.request.{CompoundPoemRequest, CreateUserRequest}
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}

class CompoundPoemApi[F[_]: Sync](controller: CompoundPoemController[F]) {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  /**
   * curl -d '{"initialLine":"initial line", "initialAuthor":"author1", "inspiredLine":"inspired line", "inspiredAuthor":"author2" }' -X POST localhost:8027/compound
   */
  val routes: HttpRoutes[F] = HttpRoutes.of {
    case rawRequest@POST -> Root / "compound" => {
      val requestAttempt: F[CompoundPoemRequest] = rawRequest.as[CompoundPoemRequest]
      val request = CompoundPoemRequest("line1", "line2", "author1", "author2")
      val x: F[CompoundPoem] = controller.save(request)
      Created(x)
    }

//    case GET -> Root / "doobie" => {
//      repository.test.map{ e => e.fold(_ => Ok("fail"), t => Ok(t))}.flatten
//    }
  }
}
