import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Sync, Timer}
import cats.implicits.catsSyntaxApplicativeId
import client.PoemClient
import config.{DatabaseConfig, ServerConfig}
import db.Transaction
import fs2.Stream
import http.CompoundPoemApi
import org.http4s.Uri
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import monix.execution.Scheduler.Implicits.global
import repository.CompoundPoemRepository
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader

object Server {

  def getDatabase[F[_]: Sync : ConcurrentEffect : Timer : ContextShift]: Stream[F, Transaction[F]] = for {
    databaseConfig <- Stream.eval(ConfigSource.default.loadOrThrow[DatabaseConfig].pure[F])
    dbConnection <- Stream.eval(new Transaction[F](databaseConfig).pure[F])
  } yield dbConnection

  def getClient[F[_]: Sync : ConcurrentEffect]: Stream[F, PoemClient[F]] = for {
    client <- BlazeClientBuilder[F](global).stream
    poemClient <- Stream.eval(new PoemClient(client,
      Uri.unsafeFromString("https://poetrydb.org/")).pure[F])
  } yield poemClient

  def stream[F[_] : ConcurrentEffect : Timer : ContextShift]: Stream[F, ExitCode] = {
    for {
      poemClient <- getClient
      database <- getDatabase
      serverConfig <- Stream.eval(ConfigSource.default.loadOrThrow[ServerConfig].pure[F])
      poemRepository <- Stream.eval(new CompoundPoemRepository[F](database).pure[F])
      routes <- Stream.eval(new CompoundPoemApi[F](poemClient, poemRepository).routes.orNotFound.pure[F])
      exitCode <- BlazeServerBuilder[F](global).bindHttp(
        serverConfig.port, serverConfig.host).withHttpApp(routes).serve
    } yield exitCode
  }
}

/*
https://http4s.org/v0.20/streaming/
pure[F] => def pure[F[_]](implicit F: Applicative[F]): F[A] = F.pure(a)
      ie: client.TestClient[F] => F[client.TestClient[F]]; Kleisli[F, Request[F], Response[F]] => F[Kleisli[F, Request[F], Response[F]]]
stream: Returns the backend as a single-element stream. The stream does not emit until the backend is ready to process requests. The backend is shut down when the stream is finalized.
Stream.eval: Creates a single element stream that gets its value by evaluating the supplied effect. If the effect fails, the returned stream fails.
      ie: client.TestClient[F] => Stream[client.TestClient[F]]; F[Kleisli[F, Request[F], Response[F]]] => Stream[F, Kleisli[F, Request[F], Response[F]]]
 */
