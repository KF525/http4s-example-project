package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class InitialLine(author: Author, line: Line)

object InitialLine {
  implicit val encoder: Encoder.AsObject[InitialLine] = deriveEncoder[InitialLine]
  implicit val decoder: Decoder[InitialLine] = deriveDecoder[InitialLine]
}
