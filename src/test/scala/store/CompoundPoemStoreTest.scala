package store

import java.sql.SQLException
import doobie.util.transactor.Transactor
import monix.eval.Task
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import monix.execution.Scheduler.Implicits.global
import org.mockito.Mock
import scala.concurrent.Await
import scala.concurrent.duration._

class CompoundPoemStoreTest extends AnyFlatSpec with should.Matchers {
//  val transactor = Mock[Transactor]
//  val repository = new CompoundPoemRepository[Task](transactor)
//
//  "poemRepository" should "work" in {
//    val x: Task[Either[SQLException, Int]] = repository.test
//
//    val y = Await.result(x.runToFuture, 5.seconds)
//    println(y)
//  }

//  it should "drop and create a table" in {
//    val drop =
//      sql"""
//    DROP TABLE IF EXISTS test_table_two;
//  """.update.run
//
//    val create =
//      sql"""
//    CREATE TABLE test_table_two (
//      name VARCHAR NOT NULL UNIQUE,
//      age  SMALLINT
//    );
//  """.update.run
//
//    val x = (drop, create).mapN(_ + _).transact(transactor.mxa)
//    val y = Await.result(x.runToFuture, 5.seconds)
//    println(y)
//  }
//
//  it should "insert into and retrieve from a table" in {
//    val insert = sql"""INSERT INTO test_table_two VALUES ('kate', 39);""".update.run
//    println("HERE I AM!!!!!!!")
//    println(transactor.mxa)
//    //    val  x = insert.transact(transactor.mxa)
//    //    val y = Await.result(x.runToFuture, 5.seconds)
//    //    println(y)
//
//    case class Person(name: String, age: Int)
//    val get = sql"""SELECT * FROM test_table_two limit 1;""".query[Person]
//    val a: doobie.ConnectionIO[List[Person]] = get.to[List]
//    val b: Task[List[Person]] = a.transact(transactor.mxa)
//    val c = Await.result(b.runToFuture, 5.seconds)
//    println(c)
//  }
}
