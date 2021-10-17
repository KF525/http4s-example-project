package client

import model.reponse.PoemResponse
import org.http4s._
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import zio.duration.durationInt
import error.PoemFailure
import error.PoemFailure.{PoemBadResponseFailure, PoemTimedOutWithoutResponseFailure}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.ziotestsyntax.ZioTestSyntax.{OngoingZioStubbingHelper, ZioTestHelper}
import zio.Task

class PoemClientSpec extends AnyFlatSpec with Matchers with Http4sDsl[Task] {
  implicit val encoder: EntityEncoder[Task, List[PoemResponse]] = jsonEncoderOf[Task, List[PoemResponse]]

  val poem = List(PoemResponse("Emily Dickinson", "I hide myself within my flower,",
    List("I hide myself within my flower,", "That fading from your Vase,", "You, unsuspecting, feel for me --",
      "Almost a loneliness."), 4))

  "PoemClient" should "respond successfully" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val expectedUri = Uri.unsafeFromString("https://www.poem.com/random/1")
    val mockHttp4sClient = mock[Http4sClient]
    when(mockHttp4sClient.getRequest(baseUri)).thenSucceed(poem)
    val client = new PoemClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)

    client.makeRequest.unsafeRun
  }

  it should "handle errors when it fails" in {
    val baseUri = Uri.unsafeFromString("https://www.poetry.com")
    val mockHttp4sClient = mock[Http4sClient]
    when(mockHttp4sClient.getRequest(baseUri)).thenFail(PoemBadResponseFailure("not ok", BadRequest))
    val client = new PoemClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)

    val failure: PoemFailure = client.makeRequest.runFailure

    failure should be(PoemBadResponseFailure("not ok", Status.BadRequest))
  }

  it should "handle errors when it fails with Timeout" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val mockHttp4sClient = mock[Http4sClient]
    when(mockHttp4sClient.getRequest(baseUri)).thenFail(PoemTimedOutWithoutResponseFailure)
    val client = new PoemClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)

    val failure: PoemFailure = client.makeRequest.runFailure

    failure should be(PoemTimedOutWithoutResponseFailure)
  }
}
