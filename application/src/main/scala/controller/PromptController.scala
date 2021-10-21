package controller

import model.{Author, CompoundPoem, FirstLine, Line, Poem, SecondLine}
import model.reponse.PromptResponse
import client.PromptClient
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import error.CompoundPoemFailure.NoPoemError
import model.request.CompoundPoemRequest
import zio.{Task, ZIO}

import scala.util.Random

class PromptController(client: PromptClient, clock: Clock, console: Console) {

  def getPrompt: Task[PromptResponse] = for {
    line <- client.makeRequest.provide(clock ++ console).map(_.headOption).flatMap {
      case Some(p) =>
        val poem = Poem.createPoem(p)
        val line = poem.lines.apply(Random.nextInt(poem.lines.size))
        ZIO.succeed(PromptResponse(poem, line))
      case None =>
        ZIO.fail(NoPoemError)
    }
  } yield line
}
