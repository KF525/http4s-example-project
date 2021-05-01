package repository

import cats.effect.Sync
import db.Transaction
import doobie.implicits.toSqlInterpolator
import doobie.free.connection.ConnectionIO
import doobie.implicits._

class CompoundPoemRepository[F[_]: Sync](database: Transaction[F]) {

  def test: F[(Int, Double)] = {
    val program3: ConnectionIO[(Int, Double)] =
      for {
        a <- sql"select 42".query[Int].unique
        b <- sql"select random()".query[Double].unique
      } yield (a, b)

    program3.transact(database.mxa)
  }

  def savePoem = ???
  def getPoem = ???
  def deletePoem = ???
}

//    case GET -> Root / "create" / "test" => {
//      println("I made it to the right place to the test!")
//      println(transactor);
//      val drop =
//        sql"""
//    DROP TABLE IF EXISTS test_table
//  """.update.run
//
//      val create =
//        sql"""
//    CREATE TABLE test_table (
//      id   SERIAL,
//      name VARCHAR NOT NULL UNIQUE,
//      age  SMALLINT
//    )
//  """.update.run
//println("About to for expression it")
//      for {
//        i <- (drop, create).mapN(_ + _).transact(transactor.mxa)
//        response <- Ok(i)
//      } yield response
//    }