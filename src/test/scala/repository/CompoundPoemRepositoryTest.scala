package repository

import java.sql.SQLException

import config.DatabaseConfig
import db.Transaction
import monix.eval.Task
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.{DAYS, DurationInt}

class CompoundPoemRepositoryTest extends AnyFlatSpec with should.Matchers{
//  val databaseConfig = DatabaseConfig("org.postgresql.Driver", "jdbc:postgresql:test_db", "root", "unicorn", 10, 3, 5)
//  val transactor = new Transaction[Task](databaseConfig).createTransactor
//  val repository = new CompoundPoemRepository[Task](transactor)
//
//  "poemRepository" should "work" in {
//    val x: Task[Either[SQLException, Int]] = repository.test
//    val y = Await.result(x.runToFuture, 5.seconds)
//    println(y)
//    //y._1 shouldBe 42
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
