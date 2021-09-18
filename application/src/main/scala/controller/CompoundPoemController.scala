package controller

import cats.effect.Sync
import model.{Author, CompoundPoem, FirstLine, SecondLine, Line}
import model.request.CompoundPoemRequest
import store.CompoundPoemStore

class CompoundPoemController[F[_]: Sync](compoundPoemStore: CompoundPoemStore[F]) {

  def save(request: CompoundPoemRequest): F[CompoundPoem] = {
    val compoundPoem = CompoundPoem(
      FirstLine(Author(request.firstAuthor), Line(request.firstLine)),
      SecondLine(Author(request.secondAuthor), Line(request.secondLine))
    )
   compoundPoemStore.save(compoundPoem)
  }
}