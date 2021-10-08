package zio.client

import model.reponse.PoemResponse
import org.http4s.Uri
import org.http4s.dsl.Http4sDsl
import zio.duration.durationInt
import zio.error.{PoemBadResponseFailure, PoemFailure, PoemTimedOutWithoutResponseFailure}
import zio.ziohttp4stest.ClientTestingHelper.withResponse
import zio.{BaseSpec, Task}
import zio.interop.catz._
import zio.ziotestsyntax.ZioTestSyntax.ZioTestHelper
import org.http4s._
import org.http4s.circe.jsonEncoderOf

class PoemClientSpec extends BaseSpec with Http4sDsl[Task] {
  implicit val encoder: EntityEncoder[Task, List[PoemResponse]] = jsonEncoderOf[Task, List[PoemResponse]]

  val poem = List(PoemResponse("Emily Dickinson", "I hide myself within my flower,",
    List("I hide myself within my flower,", "That fading from your Vase,", "You, unsuspecting, feel for me --",
      "Almost a loneliness."), 4))

  "PoemClient" should "respond successfully" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val expectedUri = Uri.unsafeFromString("https://www.poem.com/random/1")

    val makeRequest = withResponse(Ok(poem)) { client =>
      val poem = new PoemClient(client, baseUri)
      poem.makeRequest
    }

    val (request, _) = makeRequest.unsafeRun

    request.uri shouldBe expectedUri
    request.method shouldBe Method.GET
  }

  it should "handle errors when it fails" in {
    val baseUri = Uri.unsafeFromString("https://www.poetry.com")
    val contentTypeString = "text/plain"
    //val expectedContentType = `Content-Type`(MediaType.unsafeParse(contentTypeString))
    val expectedBody = "body"

    val makeRequest = withResponse(BadRequest("Not ok")) { client =>
      val notifier = new PoemClient(client, baseUri)
      notifier.makeRequest
    }

    val failure: PoemFailure = makeRequest.runFailure

    failure should be(PoemBadResponseFailure("Not ok", Status.BadRequest))
  }

  it should "handle errors when it fails with Timeout" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    //val contentTypeString = "text/plain"
    //val expectedContentType = `Content-Type`(MediaType.unsafeParse(contentTypeString))
    val expectedBody = "body"

    val makeRequest = withResponse(Ok(poem), 10.second) { client =>
      val notifier = new PoemClient(client, baseUri, 0, 0.seconds, 10.millis)
      notifier.makeRequest
    }

    val failure: PoemFailure = makeRequest.runFailure

    failure should be(PoemTimedOutWithoutResponseFailure)
  }
}
