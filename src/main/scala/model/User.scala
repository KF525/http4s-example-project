package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class User(name: String)

object User {
  implicit val encoder: Encoder.AsObject[User] = deriveEncoder[User]
  implicit val decoder: Decoder[User] = deriveDecoder[User]
}