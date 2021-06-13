package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import model.request.CompoundPoemRequest

case class CompoundPoem(initialLine: InitialLine, inspiredLine: InspiredLine)

object CompoundPoem {
  def createFromResponse(response: CompoundPoemRequest): CompoundPoem = {
    val initialLine = InitialLine(Author(response.initialAuthor), Line(response.initialLine))
    val inspiredLine = InspiredLine(Author(response.inspiredAuthor), Line(response.inspiredLine))
    CompoundPoem(initialLine, inspiredLine)
  }

  implicit val encoder: Encoder.AsObject[CompoundPoem] = deriveEncoder[CompoundPoem]
  implicit val decoder: Decoder[CompoundPoem] = deriveDecoder[CompoundPoem]
}