package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class Author(name: String)

object Author {
  implicit val encoder: Encoder.AsObject[Author] = deriveEncoder[Author]
  implicit val decoder: Decoder[Author] = deriveDecoder[Author]
}

