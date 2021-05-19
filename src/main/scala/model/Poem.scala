package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

//TODO: Replace String with Author type/lines with List[Line] type
case class Poem(author: String, title: String, lines: List[String], linecount: Int)

object Poem {
    implicit val encoder: Encoder.AsObject[Poem] = deriveEncoder[Poem]
    implicit val decoder: Decoder[Poem] = deriveDecoder[Poem]
}