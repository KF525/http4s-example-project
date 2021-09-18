package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import model.request.CompoundPoemRequest

case class CompoundPoem(firstLine: FirstLine, secondLine: SecondLine)

object CompoundPoem {
  def createFromResponse(response: CompoundPoemRequest): CompoundPoem = {
    val firstLine = FirstLine(Author(response.firstAuthor), Line(response.firstLine))
    val secondLine = SecondLine(Author(response.secondAuthor), Line(response.secondLine))
    CompoundPoem(firstLine, secondLine)
  }

  implicit val encoder: Encoder.AsObject[CompoundPoem] = deriveEncoder[CompoundPoem]
  implicit val decoder: Decoder[CompoundPoem] = deriveDecoder[CompoundPoem]
}