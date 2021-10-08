package http

import cats.effect.Sync
import controller.{CompoundPoemController}
import model.request.{CompoundPoemRequest, CreateUserRequest}
import model.{Author, CompoundPoem, Email, FirstLine, SecondLine, Line, User}
import monix.eval.Task
import org.http4s.{EntityDecoder, EntityEncoder, Request, Status, Uri}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io.POST
import org.http4s.dsl.io.GET
import org.mockito.Mockito
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import util.Http4sTestClient
import monix.execution.Scheduler.Implicits.global

class CompoundPoemApiTest extends AnyFlatSpec with MockitoSugar with Matchers with Http4sClientDsl[Task] {

  private def withMocks(test: (Http4sTestClient, CompoundPoemController[Task]) => Unit): Unit = {
    val mockController = mock[CompoundPoemController[Task]]
    val api = new CompoundPoemApi[Task](mockController)

    test(new Http4sTestClient(api.routes), mockController)
  }

  "POST" should "create a new compound poem" in withMocks { (client, mockController) =>
    implicit val encoder: EntityEncoder[Task, CompoundPoemRequest] = jsonEncoderOf[Task, CompoundPoemRequest]
    implicit val decoder: EntityDecoder[Task, CompoundPoem] = jsonOf[Task, CompoundPoem]

    val (initialLine, initialAuthor, inspiredLine, inspiredAuthor) = ("line1", "line2", "author1", "author2")
    val request = CompoundPoemRequest(initialLine, initialAuthor, inspiredLine, inspiredAuthor)
    val expectedCompoundPoem = CompoundPoem(FirstLine(Author(initialAuthor), Line(initialLine)),
      SecondLine(Author(inspiredAuthor), Line(inspiredLine)))

    Mockito.when(mockController.save(request)).thenReturn(Sync[Task].delay(expectedCompoundPoem))

    val (status, compoundPoem) = client.expect[CompoundPoem](post(compoundPoemUri, request))
    status should be(Status.Created)
    compoundPoem should be(expectedCompoundPoem)
  }

  "GET" should "return all compound poems" in withMocks { (client, mockController) =>
    implicit val decoder: EntityDecoder[Task, CompoundPoem] = jsonOf[Task, CompoundPoem]

    val (initialLine, initialAuthor, inspiredLine, inspiredAuthor) = ("line1", "line2", "author1", "author2")
    val expectedCompoundPoem = List(CompoundPoem(FirstLine(Author(initialAuthor), Line(initialLine)),
      SecondLine(Author(inspiredAuthor), Line(inspiredLine))))

    Mockito.when(mockController.view).thenReturn(Sync[Task].delay(expectedCompoundPoem))

    val (status, compoundPoem) = client.expect[CompoundPoem](get(compoundPoemUri))
    status should be(Status.Ok)
    compoundPoem should be(expectedCompoundPoem)
  }

  private val compoundPoemUri: Uri = Uri.unsafeFromString("/").addPath("compound")
  private def post[A](uri: Uri, body: A)(implicit encoder: EntityEncoder[Task, A]): Task[Request[Task]] =
    POST(body, uri) map (_ withEntity body)
  private def get[A](uri: Uri) = GET(uri)
}
