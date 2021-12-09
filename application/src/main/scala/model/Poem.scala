package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import model.reponse.PoemResponse

import scala.util.Random

case class Poem(title: String, author: Author, lines: List[Line], linecount: Int)

object Poem {
  def createPoem(poemResponse: PoemResponse): Poem = {
    val lines = poemResponse.lines.filter(_.nonEmpty).map(s => Line(s))
    Poem(poemResponse.title, Author(poemResponse.author), lines, lines.size)
  }

  def getRandomLine(poem: Poem): Line = poem.lines.apply(Random.nextInt(poem.lines.size))

  implicit val encoder: Encoder.AsObject[Poem] = deriveEncoder[Poem]
  implicit val decoder: Decoder[Poem] = deriveDecoder[Poem]
}