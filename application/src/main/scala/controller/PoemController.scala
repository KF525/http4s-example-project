package controller

import cats.ApplicativeError
import error.CompoundPoemError.NoPoemError
import cats.effect.Sync
import client.PoemClient
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import pureconfig.{ConfigReader, loadConfig}
import cats.implicits._
import model.{GetLineResponse, Poem}

import scala.util.Random

class PoemController[F[_]: Sync](poemClient: PoemClient[F]) {

  def getLine: F[GetLineResponse] =
    for {
      maybePoemResponse <- poemClient.getPoem.map(_.headOption)
      poemResponse <- ApplicativeError[F, Throwable].fromOption(maybePoemResponse, NoPoemError("No poem found"))
      poem = Poem.createPoem(poemResponse)
      line = poem.lines.apply(Random.nextInt(poem.lines.size))
    } yield GetLineResponse(poem, line)
}
