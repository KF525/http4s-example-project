import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class CovidState(state: String, hospitalizedCurrently: Int)

object CovidState {
  implicit val encoder: Encoder.AsObject[CovidState] = deriveEncoder[CovidState]
  implicit val decoder: Decoder[CovidState] = deriveDecoder[CovidState]
}