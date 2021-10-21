package controller

import model.{Author, CompoundPoem, FirstLine, Line, SecondLine}
import model.request.CompoundPoemRequest
import store.CompoundPoemStore
import zio.Task

class CompoundPoemController(compoundPoemStore: CompoundPoemStore) {

  def save(request: CompoundPoemRequest): Task[CompoundPoem] = {
    val compoundPoem = CompoundPoem(
      FirstLine(Author(request.firstAuthor), Line(request.firstLine)),
      SecondLine(Author(request.secondAuthor), Line(request.secondLine))
    )
    compoundPoemStore.create(compoundPoem)
  }

  //def view: Task[List[CompoundPoem]] = compoundPoemStore.show

}
