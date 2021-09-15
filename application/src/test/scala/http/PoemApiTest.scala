package http

import cats.effect.Sync
import client.PoemClient
import controller.PoemController
import model.reponse.PoemResponse
import model.{Author, GetLineResponse, Line, Poem}
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
    implicit val lineDecoder: EntityDecoder[Task, Line] = jsonOf[Task, Line]
    implicit val poemDecoder: EntityDecoder[Task, Poem] = jsonOf[Task, Poem]
    implicit val tupleDecoder: EntityDecoder[Task, (Poem, Line)] = jsonOf[Task, (Poem, Line)]

    val expectedPoem = Poem(Author("EmilyDickinson"), "I hide myself within my flower,",
      List(Line("I hide myself within my flower,"), Line("That fading from your Vase,"),
        Line("You, unsuspecting, feel for me --"), Line("Almost a loneliness.")), 4)
    val expectedLine = Line("You, unsuspecting, feel for me --")
    val expectedGetLineResponse = GetLineResponse(expectedPoem, expectedLine)

    Mockito.when(mockController.getLine).thenReturn(Sync[Task].delay(expectedGetLineResponse))

    val (status, response) = client.expect[(Poem, Line)](get(lineUrl))

    status should be(Status.Ok)
    response should be((expectedPoem, expectedLine))
  }

  private val lineUrl: Uri = Uri.unsafeFromString("/").addPath("line")
  private def get[A](uri: Uri): Task[Request[Task]] = GET(uri)
}
