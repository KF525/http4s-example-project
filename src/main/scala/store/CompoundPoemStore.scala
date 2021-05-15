package store

import java.sql.SQLException

import cats.effect.{ConcurrentEffect, ContextShift, IO, Sync, Timer}
import cats.free.Free
import doobie.free.connection
import doobie.implicits.toSqlInterpolator
import doobie.implicits._
import doobie.util.transactor.Transactor
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import pureconfig.loadConfig

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