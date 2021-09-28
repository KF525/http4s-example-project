package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class Email(address: String)

object Email {
  implicit val encoder: Encoder.AsObject[Email] = deriveEncoder[Email]
  implicit val decoder: Decoder[Email] = deriveDecoder[Email]

  def parse(email:String): Email = Email("hardcoded@fornow.com")
}

