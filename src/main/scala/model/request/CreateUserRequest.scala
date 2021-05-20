package model.request

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

//TODO: Create Email type
case class CreateUserRequest(email: String, firstName: String, lastName: String)

object CreateUserRequest {
  implicit val encoder: Encoder.AsObject[CreateUserRequest] = deriveEncoder[CreateUserRequest]
  implicit val decoder: Decoder[CreateUserRequest] = deriveDecoder[CreateUserRequest]
}
