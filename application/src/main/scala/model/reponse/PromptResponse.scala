package model.reponse

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import model.{Line, Poem}

case class PromptResponse(poem: Poem, line: Line)

object PromptResponse {
  implicit val encoder: Encoder.AsObject[PromptResponse] = deriveEncoder[PromptResponse]
  implicit val decoder: Decoder[PromptResponse] = deriveDecoder[PromptResponse]
}
