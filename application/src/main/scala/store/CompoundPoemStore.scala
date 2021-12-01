package store

import doobie.hikari.HikariTransactor
import doobie.Update0
import doobie.implicits._
import doobie.util.query
import model._
import zio._
import zio.interop.catz.taskConcurrentInstance

class CompoundPoemStore(transactor: HikariTransactor[Task]) {

  def create(compoundPoem: CompoundPoem): Task[CompoundPoem] =
    createQuery(compoundPoem).run.transact(transactor)
      .foldM(err => Task.fail(err), _ => Task.succeed(compoundPoem))

  def view: Task[List[CompoundPoem]] = {
    val plan = for {
      compoundPoems <- showQuery.to[List]
    } yield compoundPoems
    plan.transact(transactor)
  }

  private def createQuery(compoundPoem: CompoundPoem): Update0 = {
    val CompoundPoem(compoundPoem.title, FirstLine(
    Author(compoundPoem.firstLine.author.name),
    Line(compoundPoem.firstLine.line.text)),
    SecondLine(
    Author(compoundPoem.secondLine.author.name),
    Line(compoundPoem.secondLine.line.text))) = compoundPoem
    sql"insert into compound_poem (title, first_line, second_line, first_author, second_author) values (${compoundPoem.title}, ${compoundPoem.firstLine.line.text}, ${compoundPoem.secondLine.line.text}, ${compoundPoem.firstLine.author.name}, ${compoundPoem.secondLine.author.name})".update
  }

  private def showQuery: query.Query0[CompoundPoem] = {
    val q = sql"select title, first_line, second_line, first_author, second_author from compound_poem"
    q.query[(String,String, String, String, String)].map { case (title, firstLine, secondLine, firstAuthor, secondAuthor) =>
      CompoundPoem(title, FirstLine(Author(firstAuthor), Line(firstLine)), SecondLine(Author(secondAuthor), Line(secondLine)))}
  }
}