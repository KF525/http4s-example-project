package controller

import model.{CompoundPoem, FirstLine, SecondLine}
import model.request.CompoundPoemRequest
import store.CompoundPoemStore
import cats.effect.Sync
import model.{Author, Line}
import monix.eval.Task
import org.mockito.Mockito
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class CompoundPoemControllerTest extends AnyFlatSpec with MockitoSugar with Matchers {

  private def withMocks(test: (CompoundPoemController[Task], CompoundPoemStore[Task]) => Unit): Unit = {
    val mockStore = mock[CompoundPoemStore[Task]]
    val controller = new CompoundPoemController[Task](mockStore)

    test(controller, mockStore)
  }

  "CompoundPoemController" should "save a compound poem" in withMocks {
    (controller, mockStore) => {

      val expectedCompoundPoem = CompoundPoem(
        FirstLine(Author("Emily Dickinson"), Line("I hide myself within my flower.")),
        SecondLine(Author("Me"), Line("Each petal held up quiet the fingertip"))
      )

      val compoundPoemRequest = CompoundPoemRequest("I hide myself within my flower.",
        "Each petal held up quiet the fingertip", "Emily Dickinson", "Me")

      Mockito.when(mockStore.save(expectedCompoundPoem)).thenReturn(Sync[Task].delay(expectedCompoundPoem))

      val compoundPoem = futureValue(controller.save(compoundPoemRequest))

      compoundPoem should be(expectedCompoundPoem)
    }
  }

  private def futureValue[A](response: Task[CompoundPoem]): CompoundPoem =
        Await.result(response.runToFuture, Duration.fromNanos(1000L))
}
