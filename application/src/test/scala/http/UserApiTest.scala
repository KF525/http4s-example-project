package http

import cats.effect.Sync
import controller.UserController
import model.{Email, User}
import model.request.CreateUserRequest
import monix.eval.Task
import org.http4s.client.dsl.Http4sClientDsl
import org.mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.http4s.{EntityDecoder, EntityEncoder, Request, Status, Uri}
import util.Http4sTestClient
import org.http4s.circe._
import org.http4s.dsl.io._
import org.scalatestplus.mockito.MockitoSugar
import monix.execution.Scheduler.Implicits.global

class UserApiTest extends AnyFlatSpec with MockitoSugar with Matchers with Http4sClientDsl[Task] {

  private def withMocks(test: (Http4sTestClient, UserController[Task]) => Unit): Unit = {
    val mockController = mock[UserController[Task]]
    val api = new UserApi[Task](mockController)

    test(new Http4sTestClient(api.routes), mockController)
  }

  "POST" should "create a new user" in withMocks { (client, mockController) =>
    implicit val encoder: EntityEncoder[Task, CreateUserRequest] = jsonEncoderOf[Task, CreateUserRequest]
    implicit val decoder: EntityDecoder[Task, User] = jsonOf[Task, User]

    val (firstName, lastName, email) = ("test", "user", "testuser@gmail.com")
    val request = CreateUserRequest(email, firstName, lastName)
    val expectedUser = User(firstName, lastName, Email(email))

    Mockito.when(mockController.create(request)).thenReturn(Sync[Task].delay(expectedUser))

    val (status, user) = client.expect[User](post(newUserUri, request))
    status should be(Status.Created)
    user should be(expectedUser)
  }

//  it should "delete a user" in withMocks { (client, mockController) =>
//    implicit val encoder: EntityEncoder[Task, CreateUserRequest] = jsonEncoderOf[Task, CreateUserRequest]
//    implicit val decoder: EntityDecoder[Task, User] = jsonOf[Task, User]
//
//    val (firstName, lastName, email) = ("test", "user", "testuser@gmail.com")
//    val request = CreateUserRequest(email, firstName, lastName)
//    val expectedUser = User(firstName, lastName, Email(email))
//
//    Mockito.when(mockController.delete(request)).thenReturn(Sync[Task].delay(expectedUser))
//
//    val (status, user) = client.expect[User](delete(newUserUri, request))
//    status should be(Status.Gone)
//    user should be(expectedUser)
//  }

  private val newUserUri: Uri = Uri.unsafeFromString("/").addPath("user")
  private def post[A](uri: Uri, body: A)(implicit encoder: EntityEncoder[Task, A]): Task[Request[Task]] =
    POST(body, uri) map (_ withEntity body)
  private def delete[A](uri: Uri, body: A)(implicit encoder: EntityEncoder[Task, A]): Task[Request[Task]] =
    DELETE(body, uri) map (_ withEntity body)
}
