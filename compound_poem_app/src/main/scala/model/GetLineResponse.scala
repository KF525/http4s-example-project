package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class GetLineResponse(poem: Poem, line: Line)

object GetLineResponse {
  implicit val encoder: Encoder.AsObject[GetLineResponse] = deriveEncoder[GetLineResponse]
  implicit val decoder: Decoder[GetLineResponse] = deriveDecoder[GetLineResponse]
}
