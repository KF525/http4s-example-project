package controller

import model.request.CompoundPoemRequest
import model._
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify}
import org.scalatestplus.mockito.MockitoSugar.mock
import store.CompoundPoemStore
import util.BaseSpec
import ziotestsyntax.ZioTestSyntax.{OngoingZioStubbingHelper, ZioTestHelper}

class CompoundPoemControllerTest extends BaseSpec {

  "save" should "persist the compound poem" in {
    val title = "title"
    val author = "author"
    val line = "line1"
    val request = CompoundPoemRequest(Some(title), line, line, author, author)
    val expectedCompoundPoem = CompoundPoem(title,
      FirstLine(Author(author), Line(line)),
      SecondLine(Author(author), Line(line)))

    val store = mock[CompoundPoemStore]
    val controller = new CompoundPoemController(store)

    Mockito.when(store.create(expectedCompoundPoem)).thenSucceed(expectedCompoundPoem)

    val compoundPoem = controller.save(request).unsafeRun

    compoundPoem should be(expectedCompoundPoem)
    verify(store, times(1)).create(expectedCompoundPoem)
  }

  it should "handle errors" in {
    val title = "title"
    val author = "author"
    val line = "line1"
    val request = CompoundPoemRequest(Some(title), line, line, author, author)
    val expectedCompoundPoem = CompoundPoem(title,
      FirstLine(Author(author), Line(line)),
      SecondLine(Author(author), Line(line)))
    val error = new RuntimeException("Nope")

    val store = mock[CompoundPoemStore]
    val controller = new CompoundPoemController(store)

    Mockito.when(store.create(expectedCompoundPoem)).thenFail(error)

    val failure = controller.save(request).runFailure

    failure should be(error)
    verify(store, times(1)).create(expectedCompoundPoem)
  }
}
