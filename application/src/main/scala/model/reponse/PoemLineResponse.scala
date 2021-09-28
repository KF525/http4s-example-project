package model.reponse

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import model.{Line, Poem}

case class PoemLineResponse(poem: Poem, line: Line)

object PoemLineResponse {
  implicit val encoder: Encoder.AsObject[PoemLineResponse] = deriveEncoder[PoemLineResponse]
  implicit val decoder: Decoder[PoemLineResponse] = deriveDecoder[PoemLineResponse]
}
