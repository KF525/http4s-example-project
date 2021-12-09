package client

import util.BaseSpec
import cats.data.NonEmptyList
import error.CompoundPoemFailure.{NoPoemError, PoemBadResponseFailure, PoemTimedOutWithoutResponseFailure}
import model.reponse.PoemResponse
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.http4s._
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import zio._
import zio.clock.Clock
import zio.duration.durationInt
import ziotestsyntax.ZioTestSyntax.{OngoingZioStubbingHelper, ZioTestHelper, consecutively}

class PromptClientSpec extends BaseSpec with Http4sDsl[Task] {

  behavior of "PromptClient"

  implicit val encoder: EntityEncoder[Task, List[PoemResponse]] = jsonEncoderOf[Task, List[PoemResponse]]

  val poem = List(PoemResponse("I hide myself within my flower,", "Emily Dickinson",
    List("I hide myself within my flower,", "That fading from your Vase,", "You, unsuspecting, feel for me --",
      "Almost a loneliness."), 4))

  it should "respond successfully" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val mockHttp4sClient = mock[Http4sThinClient]

    when(mockHttp4sClient.getRequest(baseUri)).thenSucceed(poem)
    val client =  new PromptClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)

    val poemResponse: PoemResponse = client.makeRequest.unsafeRun
    poemResponse should be(poem.head)
  }

  it should "handle empty responses" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val mockHttp4sClient = mock[Http4sThinClient]

    when(mockHttp4sClient.getRequest(baseUri)).thenSucceed(List())
    val client =  new PromptClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)

    client.makeRequest.runFailure should be(NoPoemError)
  }

  it should "retry non-client errors" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val mockHttp4sClient = mock[Http4sThinClient]

    val expectedFailure = PoemBadResponseFailure("Not ok", Status.InternalServerError)
    val expectedResponses = for {
      refConsecutiveResponses <- Ref.make(NonEmptyList.of(
          ZIO.fail(expectedFailure),
          ZIO.succeed(poem)
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
    val mockHttp4sClient = mock[Http4sThinClient]

    val expectedFailure = PoemBadResponseFailure("Not ok", Status.InternalServerError)
    when(mockHttp4sClient.getRequest(baseUri)).thenFail(expectedFailure)

    val client = new PromptClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)

    client.makeRequest.runFailure should be(expectedFailure)
  }

  it should "not retry on client errors" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val mockHttp4sClient = mock[Http4sThinClient]

    val expectedFailure = PoemBadResponseFailure("Not ok", Status.BadRequest)
    when(mockHttp4sClient.getRequest(baseUri)).thenFail(expectedFailure)

    val client = new PromptClient(mockHttp4sClient, baseUri, 1, 1.second, 1.second)
    val failure = client.makeRequest.runFailure
    failure should be(expectedFailure)
  }

  it should "handle errors when all attempts timeout" in {
    val baseUri = Uri.unsafeFromString("https://www.poem.com")
    val mockHttp4sClient = mock[Http4sThinClient]

    val test = for {
      clock <- ZIO.environment[Clock]
      delayedResponse = ZIO.succeed(poem).delay(100.millis).provide(clock)
      _ = when(mockHttp4sClient.getRequest(baseUri)).thenReturn(delayedResponse)
      client = new PromptClient(mockHttp4sClient, baseUri, 0, 0.second, 10.millis)
      result <- client.makeRequest
    } yield result

    val failure = test.runFailure
    failure should be(PoemTimedOutWithoutResponseFailure)
  }
}
