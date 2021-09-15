package model.request

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class CompoundPoemRequest(initialLine: String, inspiredLine: String, initialAuthor: String, inspiredAuthor: String)

object CompoundPoemRequest {
  implicit val encoder: Encoder.AsObject[CompoundPoemRequest] = deriveEncoder[CompoundPoemRequest]
  implicit val decoder: Decoder[CompoundPoemRequest] = deriveDecoder[CompoundPoemRequest]
}