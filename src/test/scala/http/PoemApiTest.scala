package http

import cats.effect.Sync
import controller.PoemController
import model.Line
import monix.eval.Task
import org.http4s.{EntityDecoder, Request, Status, Uri}
import org.http4s.circe._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io.GET
import org.mockito.Mockito
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import util.Http4sTestClient
import monix.execution.Scheduler.Implicits.global

class PoemApiTest extends AnyFlatSpec with MockitoSugar with Matchers with Http4sClientDsl[Task] {

  private def withMocks(test: (Http4sTestClient, PoemController[Task]) => Unit): Unit = {
    val mockController = mock[PoemController[Task]]
    val api = new PoemApi[Task](mockController)

    test(new Http4sTestClient(api.routes), mockController)
  }

  "GET line" should "return a random line from a poem" in withMocks { (client, mockController) =>
    implicit val decoder: EntityDecoder[Task, Line] = jsonOf[Task, Line]
    val expectedLine = Line("After all, the sky flashes, the great sea yearns,")

    Mockito.when(mockController.getLine).thenReturn(Sync[Task].delay(expectedLine))

    val (status, user) = client.expect[Line](get(newUserUri))
    status should be(Status.Ok)
    user should be(expectedLine)
  }

  private val newUserUri: Uri = Uri.unsafeFromString("/").addPath("line")
  private def get[A](uri: Uri): Task[Request[Task]] = GET(uri)
}
