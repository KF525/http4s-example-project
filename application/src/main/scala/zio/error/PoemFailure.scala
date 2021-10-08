package zio.error

import org.http4s.{DecodeFailure, Status}

trait PoemFailure extends Throwable
case object PoemTimedOutWithoutResponseFailure extends PoemFailure
case class PoemBadResponseFailure(message: String, statusCode: Status) extends PoemFailure
case object NoPoemError extends PoemFailure
case class JsonDecodeError(f: DecodeFailure) extends PoemFailure
