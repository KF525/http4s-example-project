package http

import org.http4s.HttpRoutes
import controller.UserController
import cats.effect.Sync
import model.request.CreateUserRequest
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}

/**
 * An api is essentially a function from request to response: Api: Request[Task] => Task[Response[Task]] (really it is a partial function because a route is not guaranteed to exist) and a request or response is essentially a stream Response/Request: Stream[Task, Byte] (edited)
 */
class UserApi[F[_] : Sync](userController: UserController[F]) {

  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  /**
   * curl -d '{"email": "testuser@gmail.com", "firstName": "test", "lastName": "user"}' -X POST localhost:8027/user
   */
  val routes: HttpRoutes[F] = HttpRoutes.of {
    case rawRequest@POST -> Root / "user" =>
    for {
      request <- rawRequest.as[CreateUserRequest]
      user <- userController.create(request)
      response <- Created(user)
    } yield response
  }
}

//      for {
//        request <- rawRequest.attemptAs[CreateUserRequest].leftMap(failedDecoder => JsonDecodeError(failedDecoder))
//        user <- userController.create(request)
//        response <- Created(user)
//      } yield response
