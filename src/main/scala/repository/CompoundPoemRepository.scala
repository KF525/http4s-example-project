package repository

import java.sql.SQLException

import cats.effect.{Async, ConcurrentEffect, ContextShift, IO, Sync, Timer}
import db.Transaction
import doobie.implicits.toSqlInterpolator
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import config.DatabaseConfig
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import monix.execution.Scheduler.Implicits.global
import monix.eval.Task
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import pureconfig.loadConfig

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import io.circe.generic.semiauto._
import org.http4s.circe._

class CompoundPoemRepository[F[_]: Sync: ConcurrentEffect : Timer : ContextShift](database: Transaction[F]) {

  def test: F[Either[SQLException, Int]] = {
    println(database)
    val program3: ConnectionIO[Int] =
      for {
        a <- sql"select 42".query[Int].unique
        //b <- sql"select random()".query[Double].unique
      } yield a //(a, b)

    println("About to execute!")
    val x = program3.transact(database.mxa).attemptSql
    println("Finished inside test!!!!")
    println(x)
    x
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