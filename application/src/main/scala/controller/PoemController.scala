package controller

import model.{Author, CompoundPoem, FirstLine, Line, Poem, SecondLine}
import model.reponse.PoemLineResponse
import client.PoemClient
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import error.PoemFailure.NoPoemError
import model.request.CompoundPoemRequest
import zio.{Task, ZIO}

import scala.util.Random

class PoemController(client: PoemClient, clock: Clock, console: Console) {

  def getLine: Task[PoemLineResponse] = for {
    line <- client.makeRequest.provide(clock ++ console).map(_.headOption).flatMap {
      case Some(p) =>
        val poem = Poem.createPoem(p)
        val line = poem.lines.apply(Random.nextInt(poem.lines.size))
        ZIO.succeed(PoemLineResponse(poem, line))
      case None =>
        ZIO.fail(NoPoemError)
    }
  } yield line
}
