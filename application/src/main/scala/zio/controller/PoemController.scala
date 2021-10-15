package zio.controller

import model.Poem
import model.reponse.PoemLineResponse
import zio.ZIO
import zio.client.PoemClient
import zio.clock.Clock
import zio.console.Console
import zio.error.PoemFailure
import zio.error.PoemFailure.NoPoemError

import scala.util.Random

class PoemController(client: PoemClient) {

  def getLine: ZIO[Console with Any with Clock, PoemFailure, PoemLineResponse] = {
    client.makeRequest.map(_.headOption).flatMap {
      case Some(p) => {
        val poem = Poem.createPoem(p)
        val line = poem.lines.apply(Random.nextInt(poem.lines.size))
        ZIO.succeed(PoemLineResponse(poem, line))
      }
      case None => ZIO.fail(NoPoemError)
    }
  }
}
