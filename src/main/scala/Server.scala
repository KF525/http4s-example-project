import cats.effect.{ConcurrentEffect, ExitCode, Timer}
import cats.implicits.catsSyntaxApplicativeId
import fs2.Stream
import org.http4s.Uri
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import monix.execution.Scheduler.Implicits.global

object Server {
  def stream[F[_] : ConcurrentEffect : Timer]: Stream[F, ExitCode] = {
    for {
      client <- BlazeClientBuilder[F](global).stream //Client[F] <- Stream[F, Client[F]]
      testClient <- Stream.eval(new TestClient(client,
        Uri.unsafeFromString("https://api.covidtracking.com/v1/")).pure[F]) //TestClient[F] <- Stream[F, TestClient[F]]
      routes <- Stream.eval(new TestApi[F](testClient).routes.orNotFound.pure[F]) //Kleisli[F, Request[F], Response[F] <- Stream[F, Kleisli[F, Request[F], Response[F]]]
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(routes)
        .serve
    } yield exitCode
  }
}

/*
https://http4s.org/v0.20/streaming/
pure[F] => def pure[F[_]](implicit F: Applicative[F]): F[A] = F.pure(a)
      ie: TestClient[F] => F[TestClient[F]]; Kleisli[F, Request[F], Response[F]] => F[Kleisli[F, Request[F], Response[F]]]
stream: Returns the backend as a single-element stream. The stream does not emit until the backend is ready to process requests. The backend is shut down when the stream is finalized.
Stream.eval: Creates a single element stream that gets its value by evaluating the supplied effect. If the effect fails, the returned stream fails.
      ie: TestClient[F] => Stream[TestClient[F]]; F[Kleisli[F, Request[F], Response[F]]] => Stream[F, Kleisli[F, Request[F], Response[F]]]
 */
