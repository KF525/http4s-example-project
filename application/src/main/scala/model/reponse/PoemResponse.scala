package model.reponse

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class PoemResponse(author: String, title: String, lines: List[String], linecount: Int)

object PoemResponse {
    implicit val encoder: Encoder.AsObject[PoemResponse] = deriveEncoder[PoemResponse]
    implicit val decoder: Decoder[PoemResponse] = deriveDecoder[PoemResponse]
}