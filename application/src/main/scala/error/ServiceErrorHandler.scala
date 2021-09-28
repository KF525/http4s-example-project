package error

import cats.Monad
import cats.data.EitherT
import cats.effect.Async
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxFlatten}
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, Response, Status}

class ServiceErrorHandler[F[_]] extends Http4sDsl[F] {
  def handleErrors[E](eitherT: EitherT[F, E, F[Response[F]]])(implicit m: Monad[F],
                                                              async: Async[F],
                                                              encoder: EntityEncoder[F, E],
                                                              statusForError: E => Status): F[Response[F]] = {

    def convertError(error: E): F[Response[F]] =
      async.pure(Response(status = statusForError(error), body = encoder.toEntity(error).body))

    eitherT.leftMap(convertError).merge.flatten.recoverWith {
      case e: Throwable =>
        InternalServerError(Error("An Internal Error Occurred."))
    }
  }
}
