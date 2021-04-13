import cats.effect.{ConcurrentEffect, ExitCode, Timer}
import cats.implicits.catsSyntaxApplicativeId
import fs2.Stream
import org.http4s.Uri
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import monix.execution.Scheduler.Implicits.global
import org.http4s.client.Client

object Server {
  def stream[F[_]: ConcurrentEffect: Timer]: Stream[F, ExitCode] = {
    for {
      client: Client[F] <- BlazeClientBuilder[F](global).stream
      testClient <- Stream.eval(new TestClient(client,
        Uri.unsafeFromString("https://api.covidtracking.com/v1/")).pure[F])
      routes <- Stream.eval(new TestApi[F](testClient).routes.orNotFound.pure[F])
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(routes)
        .serve
    } yield exitCode
  }
}

/*
stream: Returns the backend as a single-element stream. The stream does not emit until the backend is ready to process requests. The backend is shut down when the stream is finalized.
Stream.eval: Creates a single element stream that gets its value by evaluating the supplied effect. If the effect fails, the returned stream fails.
 */
