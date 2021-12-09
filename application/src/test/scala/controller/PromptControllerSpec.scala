package controller

import client.PromptClient
import model.{Author, Line, Poem}
import model.reponse.{PoemResponse, PromptResponse}
import org.mockito.Mockito
import org.scalatestplus.mockito.MockitoSugar.mock
import util.BaseSpec
import zio.{Task, ZIO}
import zio.clock.Clock
import zio.console.Console
import ziotestsyntax.ZioTestSyntax.{OngoingZioStubbingHelper, ZioTestHelper, consecutively}

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
      response: List[PoemResponse] = List(PoemResponse(title, author, List(line), 1))
      _ = Mockito.when(client.makeRequest).thenSucceed(response)
      promptResponse = controller.getPrompt.unsafeRun
    } yield promptResponse

    val promptResponse = result.unsafeRun
    promptResponse.poem should be(expectedPoem)
    promptResponse.line should be(Line(line))
  }
}
