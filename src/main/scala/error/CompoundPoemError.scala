package error

import org.http4s.DecodeFailure

sealed trait CompoundPoemError

object CompoundPoemError {

  case class JsonDecodeError(f: DecodeFailure) extends CompoundPoemError

}
