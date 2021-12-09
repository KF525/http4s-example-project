package controller

import client.PromptClient
import error.CompoundPoemFailure.PoemBadResponseFailure
import model.reponse.PoemResponse
import model.{Author, Line, Poem}
import org.mockito.Mockito
import org.scalatestplus.mockito.MockitoSugar.mock
import util.BaseSpec
import org.http4s._
import zio.ZIO
import zio.clock.Clock
import zio.console.Console
import ziotestsyntax.ZioTestSyntax.{OngoingZioStubbingHelper, ZioTestHelper}


class PromptControllerSpec extends BaseSpec {

  "getPrompt" should "return a poem and selected line" in {
    val title = "title"
    val author = "author"
    val line = "line1"
    val expectedPoem = Poem(title, Author(author), List(Line(line)), 1)

    val result = for {
      clock <- ZIO.environment[Clock]
      console <- ZIO.environment[Console]
      client = mock[PromptClient]
      controller = new PromptController(client, clock, console)
      poemResponse: PoemResponse = PoemResponse(title, author, List(line), 1)
      _ = Mockito.when(client.makeRequest).thenSucceed(poemResponse)
      prompt <- controller.getPrompt
    } yield prompt

    val promptResponse = result.unsafeRun
    promptResponse.poem should be(expectedPoem)
    promptResponse.line should be(Line(line))
  }

  it should "handle errors" in {
    val expectedFailure = PoemBadResponseFailure("Not ok", Status.InternalServerError)

    val result = for {
      clock <- ZIO.environment[Clock]
      console <- ZIO.environment[Console]
      client = mock[PromptClient]
      controller = new PromptController(client, clock, console)
      _ = Mockito.when(client.makeRequest).thenFail(expectedFailure)
      response <- controller.getPrompt
    } yield response

    result.runFailure should be(expectedFailure)
  }
}
