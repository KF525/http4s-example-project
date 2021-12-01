package controller

import model.{Line, Poem}
import model.reponse.PromptResponse
import client.PromptClient
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import error.CompoundPoemFailure.NoPoemError
import zio.{Task, ZIO}

import scala.util.Random

class PromptController(client: PromptClient, clock: Clock, console: Console) {

  def getPrompt: Task[PromptResponse] = for {
    line <- client.makeRequest.provide(clock ++ console).map(_.headOption).flatMap {
      case Some(p) =>
        val poem: Poem = Poem.createPoem(p)
        val lines: List[Line] = poem.lines.filter(l => !l.text.isEmpty)
        val line: Line = lines.apply(Random.nextInt(lines.size))
        ZIO.succeed(PromptResponse(poem, line))
      case None =>
        ZIO.fail(NoPoemError)
    }
  } yield line
}
