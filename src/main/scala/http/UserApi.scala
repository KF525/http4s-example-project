package http

import cats.data.EitherT
import cats.effect.Sync
import model.{CreateUserRequest, User}
import org.http4s.HttpRoutes
import controller.UserController
import error.CompoundPoemError.JsonDecodeError
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import cats.effect.Sync
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import cats.implicits._

/**
 * An api is essentially a function from request to response: Api: Request[Task] => Task[Response[Task]] (really it is a partial function because a route is not guaranteed to exist) and a request or response is essentially a stream Response/Request: Stream[Task, Byte] (edited)
 */
class UserApi[F[_] : Sync](userController: UserController[F]) {

  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  /**
   * curl -d "{"email": "testuser@gmail.com", "firstName": "test", "lastName": "user"}" -X POST localhost:8027/user
   */
  val routes: HttpRoutes[F] = HttpRoutes.of {
    case rawRequest@POST -> Root / "user" => {
//      for {
//        request <- rawRequest.attemptAs[CreateUserRequest].leftMap(failedDecoder => JsonDecodeError(failedDecoder))
//        user <- userController.create(request)
//        response <- Created(user)
//      } yield response

      val requestAttempt: F[CreateUserRequest] = rawRequest.as[CreateUserRequest]
      println(requestAttempt)
      val request = CreateUserRequest("testuser@gmail.com", "test", "user")
      val user: F[User] = userController.create(request)
      Created(user)
    }

    //Delete-Update-Get-Search
  }
}
