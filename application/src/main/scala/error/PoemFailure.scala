package error

import org.http4s.{DecodeFailure, Status}

sealed trait PoemFailure extends Throwable

object PoemFailure {
  case object PoemTimedOutWithoutResponseFailure extends PoemFailure
  case class PoemBadResponseFailure(message: String, statusCode: Status) extends PoemFailure
  case object NoPoemError extends PoemFailure
  case class JsonDecodeError(f: DecodeFailure) extends PoemFailure
  case class PoemGeneralFailure(message: String) extends PoemFailure
}

