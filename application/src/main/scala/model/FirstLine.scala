package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class FirstLine(author: Author, line: Line)

object FirstLine {
  implicit val encoder: Encoder.AsObject[FirstLine] = deriveEncoder[FirstLine]
  implicit val decoder: Decoder[FirstLine] = deriveDecoder[FirstLine]
}
