package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class CompoundPoem(initialLine: InitialLine, inspiredLine: InspiredLine)

object CompoundPoem {
  implicit val encoder: Encoder.AsObject[CompoundPoem] = deriveEncoder[CompoundPoem]
  implicit val decoder: Decoder[CompoundPoem] = deriveDecoder[CompoundPoem]
}
