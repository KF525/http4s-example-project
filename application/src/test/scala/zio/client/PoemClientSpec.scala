package zio.client

import model.reponse.PoemResponse
import org.http4s._
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import zio.error.PoemFailure
import zio.error.PoemFailure.{PoemBadResponseFailure, PoemTimedOutWithoutResponseFailure}
import zio.ziotestsyntax.ZioTestSyntax.{OngoingZioStubbingHelper, ZioTestHelper}
import zio.{BaseSpec, Task}

class PoemClientSpec extends BaseSpec with Http4sDsl[Task] {
  implicit val encoder: EntityEncoder[Task, List[PoemResponse]] = jsonEncoderOf[Task, List[PoemResponse]]

  val poem = List(PoemResponse("Emily Dickinson", "I hide myself within my flower,",
    List("I hide myself within my flower,", "That fading from your Vase,", "You, unsuspecting, feel for me --",
      "Almost a loneliness."), 4))

  "PoemClient" should "respond successfully" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val expectedUri = Uri.unsafeFromString("https://www.poem.com/random/1")
    val mockHttp4sClient = mock[Http4sClient]
    when(mockHttp4sClient.getRequest(baseUri)).thenSucceed(poem)
    val client = new PoemClient(mockHttp4sClient, baseUri)

    client.makeRequest.unsafeRun
  }

  it should "handle errors when it fails" in {
    val baseUri = Uri.unsafeFromString("https://www.poetry.com")
    val mockHttp4sClient = mock[Http4sClient]
    when(mockHttp4sClient.getRequest(baseUri)).thenFail(PoemBadResponseFailure("not ok", BadRequest))
    val client = new PoemClient(mockHttp4sClient, baseUri)

    val failure: PoemFailure = client.makeRequest.runFailure

    failure should be(PoemBadResponseFailure("not ok", Status.BadRequest))
  }

  it should "handle errors when it fails with Timeout" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val mockHttp4sClient = mock[Http4sClient]
    when(mockHttp4sClient.getRequest(baseUri)).thenFail(PoemTimedOutWithoutResponseFailure)
    val client = new PoemClient(mockHttp4sClient, baseUri)

    val failure: PoemFailure = client.makeRequest.runFailure

    failure should be(PoemTimedOutWithoutResponseFailure)
  }
}
