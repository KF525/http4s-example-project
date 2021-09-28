package http

import cats.Monad
import cats.data.EitherT
import cats.effect.Async
import org.http4s.{EntityEncoder, Response, Status}
import org.http4s.dsl.Http4sDsl
import cats.implicits._

class ServiceErrorHandler[F[_]] extends Http4sDsl[F]{

  def handleErrors[E](eitherT: EitherT[F, E, F[Response[F]]])(implicit m: Monad[F], async: Async[F], encoder: EntityEncoder[F, E], statusForError: E => Status): F[Response[F]] = {

    def convertError(error: E): F[Response[F]] =
      async.pure(Response(status = statusForError(error), body = encoder.toEntity(error).body))
    def logError(error: E): E = {
      //logger.info(error.toString)
      error
    }

    eitherT.leftMap(logError).leftMap(convertError).merge.flatten.recoverWith {
      case e: Throwable =>
        //logger.error(s"An Internal Error Occurred: " + e.cascadingMessage + "StackTrace:" + e.stackTrace, e)
        InternalServerError("For now, this is my error")
    }
  }
}
