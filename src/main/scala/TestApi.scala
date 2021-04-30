import cats.effect.Sync
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import doobie.free.connection.ConnectionIO
import cats.implicits._
import client.PoemClient
import doobie.implicits._
import postgres.Transaction

class TestApi[F[_]: Sync](client: PoemClient[F], dbConnection: Transaction[F]) {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "health" =>  Ok("healthy")
    case GET -> Root / "covid" / state =>
      for {
        c <- client.getSomething(state)
        response <- Ok(c)
      } yield response
    case GET -> Root / "poem" =>
      for {
        poem <- client.getRandomPoem
        response <- Ok(poem)
      } yield response
    case GET -> Root / "create" / "test" => {
      println("I made it to the right place to the test!")
      println(dbConnection);
      val drop =
        sql"""
    DROP TABLE IF EXISTS test_table
  """.update.run

      val create =
        sql"""
    CREATE TABLE test_table (
      id   SERIAL,
      name VARCHAR NOT NULL UNIQUE,
      age  SMALLINT
    )
  """.update.run
println("About to for expression it")
      for {
        i <- (drop, create).mapN(_ + _).transact(dbConnection.mxa)
        response <- Ok(i)
      } yield response
    }
    case GET -> Root / "doobie" => {
      println("inside the doobie")
      println(dbConnection.mxa)
      val program3: ConnectionIO[(Int, Double)] =
        for {
          a <- sql"select 42".query[Int].unique
          b <- sql"select random()".query[Double].unique
        } yield (a, b)

      for {
        tuple <- program3.transact(dbConnection.mxa)
        response <- Ok(tuple)
      } yield response
    }
    case GET -> Root / line =>
      for {
        text <- client.getLine(line)
        response <- Ok(text)
      } yield response
  }
}
