package store

import doobie.implicits.toSqlInterpolator
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import pureconfig.loadConfig
import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import cats.free.Free
import doobie.free.connection
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import model.{Author, CompoundPoem, FirstLine, Line, SecondLine, User}
import doobie.implicits._
import cats.implicits._
import doobie.util.query

class CompoundPoemStore[F[_]: Sync: ConcurrentEffect : Timer : ContextShift](transactor: Transactor[F]) {

  def save(compoundPoem: CompoundPoem): F[CompoundPoem] = {
    val plan: Free[connection.ConnectionOp, Unit] = for {
      _ <-  createQuery(compoundPoem).run
    } yield ()
    plan.transact(transactor).map(_ => compoundPoem)
  }

  def view: F[List[CompoundPoem]] = {
    val plan = for {
      compoundPoems <-  showQuery.to[List]
    } yield compoundPoems
    plan.transact(transactor)
  }

  private def createQuery(compoundPoem: CompoundPoem): Update0 = {
    val CompoundPoem(FirstLine(
    Author(compoundPoem.firstLine.author.name),
    Line(compoundPoem.firstLine.line.text)),
    SecondLine(
    Author(compoundPoem.secondLine.author.name),
    Line(compoundPoem.secondLine.line.text))) = compoundPoem
    sql"insert into compound_poem (first_line, second_line, first_author, second_author) values (${compoundPoem.firstLine.line.text}, ${compoundPoem.secondLine.line.text}, ${compoundPoem.firstLine.author.name}, ${compoundPoem.secondLine.author.name})".update
  }

  private def showQuery: query.Query0[CompoundPoem] = {
    val q = sql"select first_line, second_line, first_author, second_author from compound_poem"
    q.query[(String, String, String, String)].map { case (firstLine, secondLine, firstAuthor, secondAuthor) =>
      CompoundPoem(FirstLine(Author(firstAuthor), Line(firstLine)), SecondLine(Author(secondAuthor), Line(secondLine)))}
  }
}