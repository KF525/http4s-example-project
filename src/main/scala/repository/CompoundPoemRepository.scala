package repository

import java.sql.SQLException

import cats.effect.{ConcurrentEffect, ContextShift, IO, Sync, Timer}
import doobie.implicits.toSqlInterpolator
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import pureconfig.loadConfig

class CompoundPoemRepository[F[_]: Sync: ConcurrentEffect : Timer : ContextShift](database: Transactor[F]) {

  def test: F[Either[SQLException, Int]] = {
    println(database)
    val program3: ConnectionIO[Int] =
      for {
        a <- sql"select 42, 43".query[Int].unique
      } yield a
    program3.transact(database).attemptSql
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