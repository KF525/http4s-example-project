package http

import java.sql.SQLException
import io.circe.generic.semiauto._
import org.http4s.circe._
import cats.effect.Sync
import cats.implicits._
import client.PoemClient
import model.Poem
import monix.eval.Task
import org.http4s.{EntityDecoder, HttpRoutes, Response}
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import repository.CompoundPoemRepository

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import monix.execution.Scheduler.Implicits.global
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.jsonOf

class CompoundPoemApi[F[_]: Sync](client: PoemClient[F], repository: CompoundPoemRepository[F]) {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "health" =>  Ok("healthy")
    case GET -> Root / "poem" => //TODO: Get rid of the list
      val a = client.getPoem
      println(a)
      println("Here is the poem for....")
      for {
        poem <- a
        response <- Ok(poem)
      } yield response
    case GET -> Root / "doobie" => {
      println("Here I am....")
      val x = repository.test
      println(x)
      println("Here is the doobie for....")
      x.map{ e => println(e); println(e.isLeft); println(e.isRight); e.fold(_ => Ok("fail"), t => Ok(t))}.flatten
    }
  }
}
