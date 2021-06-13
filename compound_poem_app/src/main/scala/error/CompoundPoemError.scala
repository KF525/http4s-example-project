package error

import org.http4s.DecodeFailure

sealed trait CompoundPoemError extends Throwable

object CompoundPoemError {

  case class JsonDecodeError(f: DecodeFailure) extends CompoundPoemError
  case class NoPoemError(s: String) extends CompoundPoemError

}
