package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import model.reponse.PoemResponse

case class Poem(author: Author, title: String, lines: List[Line], linecount: Int)

object Poem {
  def createPoem(poemResponse: PoemResponse): Poem = {
    val lines = poemResponse.lines.map(s => Line(s))
    Poem(Author(poemResponse.author), poemResponse.title, lines, poemResponse.linecount)
  }

  implicit val encoder: Encoder.AsObject[Poem] = deriveEncoder[Poem]
  implicit val decoder: Decoder[Poem] = deriveDecoder[Poem]
}