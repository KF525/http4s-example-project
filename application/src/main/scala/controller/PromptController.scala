package controller

import model.{Line, Poem}
import model.reponse.PromptResponse
import client.PromptClient
import zio.clock.Clock
import zio.console.Console
import error.CompoundPoemFailure.NoPoemError
import zio.{Task, ZIO}

import scala.util.Random

class PromptController(client: PromptClient, clock: Clock, console: Console) {

  //TODO: Looks like I am dropping the error that may come back from makeRequest, mapError
  def getPrompt: Task[PromptResponse] = for {
    line <- client.makeRequest.provide(clock ++ console).map(_.headOption).flatMap {
      case Some(p) =>
        val poem: Poem = Poem.createPoem(p)
        val line: Line = poem.lines.apply(Random.nextInt(poem.lines.size))
        ZIO.succeed(PromptResponse(poem, line))
      case None =>
        ZIO.fail(NoPoemError)
    }
  } yield line
}
