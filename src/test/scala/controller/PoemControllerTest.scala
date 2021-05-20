package controller

import cats.effect.Sync
import client.PoemClient
import model.reponse.PoemResponse
import model.{Author, Line, Poem}
import monix.eval.Task
import org.mockito.Mockito
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import monix.execution.Scheduler.Implicits.global

class PoemControllerTest extends AnyFlatSpec with MockitoSugar with Matchers {

  private def withMocks(test: (PoemController[Task], PoemClient[Task]) => Unit): Unit = {
    val mockClient = mock[PoemClient[Task]]
    val controller = new PoemController[Task](mockClient)

    test(controller, mockClient)
  }

  "PoemController" should "return a poem and random line from that poem" in withMocks {
    (controller, mockClient) => {

    val expectedPoem = Poem(Author("Emily Dickinson"), "I hide myself within my flower,",
      List(Line("I hide myself within my flower,"), Line("That fading from your Vase,"),
        Line("You, unsuspecting, feel for me --"), Line("Almost a loneliness.")), 4)

    val poemResponse = List(PoemResponse("Emily Dickinson", "I hide myself within my flower,",
      List("I hide myself within my flower,", "That fading from your Vase,",
        "You, unsuspecting, feel for me --", "Almost a loneliness."), 4))

    Mockito.when(mockClient.getPoem).thenReturn(Sync[Task].delay(poemResponse))

    val (poem, line) = futureValue(controller.getLine)

    poem should be(expectedPoem)
    }
  }

  private def futureValue[A](response: Task[(Poem, Line)]): (Poem, Line) =
    Await.result(response.runToFuture, Duration.fromNanos(1000L))
}
