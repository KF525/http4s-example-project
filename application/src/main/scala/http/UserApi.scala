//package http
//
//import cats.Monad
//import org.http4s.HttpRoutes
//import controller.UserController
//import cats.effect.{Async, Sync}
//import model.request.CreateUserRequest
//import cats.implicits._
//import error.CompoundPoemError.JsonDecodeError
//import io.circe.Encoder._
//import io.circe.generic.auto.exportEncoder
//import org.http4s.dsl.Http4sDsl
//import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
//import org.http4s.EntityEncoder
//
//class UserApi[F[_] : Sync](userController: UserController[F],
//                           serviceErrorHandler: ServiceErrorHandler[F]) {
//
//  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
//  import dsl._
//
//  // curl -d '{"email": "testuser@gmail.com", "firstName": "test", "lastName": "user"}' -X POST localhost:8001/user
//  val routes: HttpRoutes[F] = HttpRoutes.of {
//    case rawRequest@POST -> Root / "user" =>
////      serviceErrorHandler.handleErrors {
//        for {
//          request <- rawRequest.attemptAs[CreateUserRequest].leftMap(decodeFailure => JsonDecodeError(decodeFailure))
//          user <- userController.create(request)
//        } yield Created(user)
////      }
//    case GET -> Root / "user" / IntVar(userId) =>
//      //serviceErrorHandler.handleErrors {
//        for {
//          user <- userController.get(userId)
//          response <- Ok(user)
//        } yield response
//      //}
//  }
//}
