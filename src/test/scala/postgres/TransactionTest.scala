package postgres

import cats.effect._
import config.DatabaseConfig
import doobie.implicits._
import monix.eval.Task
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import db.Transaction

class TransactionTest extends AnyFlatSpec with should.Matchers {
  val databaseConfig = DatabaseConfig(
    "org.postgresql.Driver",
    "jdbc:postgresql:test_db",
    "root",
    "unicorn",
    10, 3, 5)
  val transactor = new Transaction[Task](databaseConfig).createTransactor

  "doobie" should "work" in {
    transactor.use { xa =>
      for {
        n <- sql"select 42".query[Int].unique.transact(xa)
        _ <- Task(println(n))
      } yield ExitCode.Success
    }
  }
}

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
//    println(y) //This should be 1
//  }
//
//  it should "insert into and retrieve from a table" in {
//    val insert = sql"""INSERT INTO test_table_two VALUES ('kate', 39);""".update.run
//    println("HERE I AM!!!!!!!")
//    println(transactor.mxa)
//    val  x = insert.transact(transactor.mxa)
//    val y = Await.result(x.runToFuture, 5.seconds)
//    println(y)
//
//    case class Person(name: String, age: Int)
//    val get = sql"""SELECT * FROM test_table_two limit 1;""".query[Person]
//    val a: doobie.ConnectionIO[List[Person]] = get.to[List]
//    val b: Task[List[Person]] = a.transact(transactor.mxa)
//    val c = Await.result(b.runToFuture, 5.seconds)
//    println(c)
//  }
