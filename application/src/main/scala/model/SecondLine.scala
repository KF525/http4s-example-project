package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class SecondLine(author: Author, line: Line)

object SecondLine {
  implicit val encoder: Encoder.AsObject[SecondLine] = deriveEncoder[SecondLine]
  implicit val decoder: Decoder[SecondLine] = deriveDecoder[SecondLine]
}
