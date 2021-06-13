package store

import java.sql.SQLException
import doobie.implicits.toSqlInterpolator
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import pureconfig.loadConfig
import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import cats.free.Free
import doobie.free.connection
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import model.{Author, CompoundPoem, InitialLine, InspiredLine, Line, User}
import doobie.implicits._
import cats.implicits._

class CompoundPoemStore[F[_]: Sync: ConcurrentEffect : Timer : ContextShift](transactor: Transactor[F]) {

  def test: F[Either[SQLException, Int]] = {
    println(transactor)
    val program3: Free[connection.ConnectionOp, Int] =
      for {
        a <- x.unique
      } yield a
    program3.transact(transactor).attemptSql
  }

  def x: doobie.Query0[Int] = sql"select 42, 43".query[Int]

//  def save(compoundPoemEntry: CompoundPoemRequest): F[CompoundPoemResponse] =
//    Sync[F].delay(CompoundPoemResponse("first line", "second line", "author 1", "author2"))

  def save(compoundPoem: CompoundPoem): F[CompoundPoem] = {
    val plan: Free[connection.ConnectionOp, Unit] = for {
      _ <-  createQuery(compoundPoem).run
    } yield ()

    plan.transact(transactor).map(_ => compoundPoem)
  }

  private def createQuery(compoundPoem: CompoundPoem): Update0 = {
    val CompoundPoem(InitialLine(
    Author(compoundPoem.initialLine.author.name),
    Line(compoundPoem.initialLine.line.text)),
    InspiredLine(
    Author(compoundPoem.inspiredLine.author.name),
    Line(compoundPoem.inspiredLine.line.text))) = compoundPoem
    sql"insert into compound_poem (initial_line, inspired_line, initial_author, inspired_author) values (${compoundPoem.initialLine.line.text}, ${compoundPoem.inspiredLine.line.text}, ${compoundPoem.initialLine.author.name}, ${compoundPoem.inspiredLine.author.name})".update
  }
}