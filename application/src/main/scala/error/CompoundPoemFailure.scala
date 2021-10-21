package error

import org.http4s.{DecodeFailure, Status}

sealed trait CompoundPoemFailure extends Throwable

object CompoundPoemFailure {
  case object PoemTimedOutWithoutResponseFailure extends CompoundPoemFailure
  case class PoemBadResponseFailure(message: String, statusCode: Status) extends CompoundPoemFailure
  case object NoPoemError extends CompoundPoemFailure
  case class JsonDecodeError(f: DecodeFailure) extends CompoundPoemFailure
  case class PoemGeneralFailure(message: String) extends CompoundPoemFailure
}

