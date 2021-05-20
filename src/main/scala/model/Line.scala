package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class Line(text: String)

object Line {
  implicit val encoder: Encoder.AsObject[Line] = deriveEncoder[Line]
  implicit val decoder: Decoder[Line] = deriveDecoder[Line]
}