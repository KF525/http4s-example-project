package zio.controller

import cats.ApplicativeError
import model.{Poem, reponse}
import model.reponse.PoemLineResponse
import org.http4s.Uri
import zio.{Task, ZIO}
import zio.client.PoemClient
import zio.clock.Clock
import zio.console.Console
import zio.interop.catz._
import zio.error.{NoPoemError, PoemFailure}

import scala.util.Random

class PoemController(client: PoemClient) {

  //TODO: Throwable => PoemFailure
  def getLine: ZIO[Console with Any with Clock, Throwable, PoemLineResponse] =
    for {
      maybePoemResponse <- client.makeRequest.map(_.headOption)
      poemResponse <- ApplicativeError[Task, Throwable].fromOption(maybePoemResponse, NoPoemError)
      poem = Poem.createPoem(poemResponse)
      line = poem.lines.apply(Random.nextInt(poem.lines.size))
    } yield reponse.PoemLineResponse(poem, line)
}
