package zio.store

import doobie.Transactor
import doobie.Update0
import doobie.Query0
import zio.interop.catz._
import doobie.implicits._
import zio._
//import doobie.util.query
//import doobie.util.transactor.Transactor
//import doobie.util.update.Update0
import model._
//import pureconfig.generic.auto.exportReader
//import pureconfig.{ConfigSource, loadConfig}
//import zio.{Runtime, Task, ZIO}
import zio.interop.catz.taskConcurrentInstance

class CompoundPoemStore(transactor: Transactor[Task]) {

  //implicit val runtime: Runtime[Any] = Runtime.default

  def create(compoundPoem: CompoundPoem): Task[CompoundPoem] = {
    val x: Any =
  createQuery(compoundPoem).run.transact {
        transactor
      }
        .foldM(err => Task.fail(err), _ => Task.succeed(compoundPoem))
  }


  //  def save(compoundPoem: CompoundPoem): Task[CompoundPoem] = {
//    val plan: Free[connection.ConnectionOp, Unit] = for {
//      _ <-  createQuery(compoundPoem).run
//    } yield ()
//    plan.transact(transactor).map(_ => compoundPoem)
//  }
//
//  def view: Task[List[CompoundPoem]] = {
//    val plan = for {
//      compoundPoems <-  showQuery.to[List]
//    } yield compoundPoems
//    plan.transact(transactor)
//  }

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