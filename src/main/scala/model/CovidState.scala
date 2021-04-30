package model

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class CovidState(state: String, hospitalizedCurrently: Int)

object CovidState {
  implicit val encoder: Encoder.AsObject[CovidState] = deriveEncoder[CovidState]
  implicit val decoder: Decoder[CovidState] = deriveDecoder[CovidState]
}