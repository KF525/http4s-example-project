package controller

import error.CompoundPoemError.NoPoemError
import cats.effect.Sync
import client.PoemClient
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import pureconfig.{ConfigReader, loadConfig}
import cats.implicits._
import model.Poem

import scala.util.Random

class PoemController[F[_]: Sync](poemClient: PoemClient[F]) {

  def getLine: F[(Poem, String)] =
    for {
      poem <- poemClient.getPoem.ensure(NoPoemError("No poem found."))(_.nonEmpty)
      line <- Sync[F].delay(poem.map(p => p.lines.apply(Random.nextInt(p.lines.size))).head)
    } yield (poem.head, line)
}
