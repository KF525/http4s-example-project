package error

import io.circe.generic.semiauto._
import io.circe._

case class Error(message: String)

object Error {
  implicit val encoder: Encoder.AsObject[Error] = deriveEncoder[Error]
  implicit val decoder: Decoder[Error] = deriveDecoder[Error]
}
