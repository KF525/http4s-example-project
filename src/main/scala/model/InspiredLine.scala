package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class InspiredLine(author: Author, line: Line)

object InspiredLine {
  implicit val encoder: Encoder.AsObject[InspiredLine] = deriveEncoder[InspiredLine]
  implicit val decoder: Decoder[InspiredLine] = deriveDecoder[InspiredLine]
}
