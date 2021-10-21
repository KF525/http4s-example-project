package client

import util.BaseSpec
import cats.data.NonEmptyList
import error.CompoundPoemFailure.PoemBadResponseFailure
import model.reponse.PoemResponse
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.http4s._
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import zio._
import zio.duration.durationInt
import ziotestsyntax.ZioTestSyntax.{OngoingZioStubbingHelper, ZioTestHelper, consecutively}

class PromptClientSpec extends BaseSpec with Http4sDsl[Task] {
  implicit val encoder: EntityEncoder[Task, List[PoemResponse]] = jsonEncoderOf[Task, List[PoemResponse]]

  val poem = List(PoemResponse("Emily Dickinson", "I hide myself within my flower,",
    List("I hide myself within my flower,", "That fading from your Vase,", "You, unsuspecting, feel for me --",
      "Almost a loneliness."), 4))

  "makeRequest" should "" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val expectedUri = Uri.unsafeFromString("https://www.poem.com/random/1")
    val mockHttp4sClient = mock[Http4sClient]

    when(mockHttp4sClient.getRequest(baseUri)).thenSucceed(poem)
    val client = new PromptClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)

    client.makeRequest.unsafeRun
  }

  it should "retry non-client errors" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val mockHttp4sClient = mock[Http4sClient]

    val expectedFailure = PoemBadResponseFailure("Not ok", Status.InternalServerError)
    val expectedResponses = for {
      refConsecutiveResponses <- Ref.make(NonEmptyList.of(
          ZIO.fail(expectedFailure),
          ZIO.succeed(List())
      ))
      consecutiveResponses = consecutively(refConsecutiveResponses)
      _ = when(mockHttp4sClient.getRequest(baseUri)).thenReturn(consecutiveResponses)
      client = new PromptClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)
      _ <- client.makeRequest
    } yield ()

    expectedResponses.unsafeRun
  }

  it should "handle errors when attempts exhausted" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val mockHttp4sClient = mock[Http4sClient]

    val expectedFailure = PoemBadResponseFailure("Not ok", Status.InternalServerError)
    when(mockHttp4sClient.getRequest(baseUri)).thenFail(expectedFailure)

    val client = new PromptClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)

    client.makeRequest.runFailure should be (expectedFailure)
  }

  it should "not retry on client errors" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val mockHttp4sClient = mock[Http4sClient]

    val expectedFailure = PoemBadResponseFailure("Not ok", Status.BadRequest)
    val expectedResponses = for {
      refConsecutiveResponses <- Ref.make(NonEmptyList.of(
        ZIO.fail(expectedFailure),
        ZIO.succeed(List())
      ))
      consecutiveResponses = consecutively(refConsecutiveResponses)
      _ = when(mockHttp4sClient.getRequest(baseUri)).thenReturn(consecutiveResponses)
      client = new PromptClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)
      _ <- client.makeRequest
    } yield ()

    expectedResponses.runFailure should be (expectedFailure)
  }

  it should "handle errors when all attempts timeout" in {

  }
}
