package http

import model.User
import org.http4s.HttpRoutes
import controller.UserController
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import cats.effect.Sync
import model.request.CreateUserRequest
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

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
