package controller

import cats.effect.Sync
import model.{Author, CompoundPoem, InitialLine, InspiredLine, Line}
import model.request.CompoundPoemRequest
import store.CompoundPoemStore
import cats.implicits._

class CompoundPoemController[F[_]: Sync](compoundPoemStore: CompoundPoemStore[F]) {

  def save(request: CompoundPoemRequest): F[CompoundPoem] = {
    val compoundPoem = CompoundPoem(
      InitialLine(Author(request.initialAuthor), Line(request.initialLine)),
      InspiredLine(Author(request.inspiredAuthor), Line(request.inspiredLine))
    )
   compoundPoemStore.save(compoundPoem)
  }
}