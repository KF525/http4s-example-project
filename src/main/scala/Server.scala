import cats.effect.{ConcurrentEffect, ExitCode, Resource, Timer}
import cats.implicits.catsSyntaxApplicativeId
import fs2.Stream
import monix.eval.Task
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
      testClient <- Stream.eval(new TestClient(client, Uri(path = "https://www.gutenberg.org")).pure[F])
      routes <- Stream.eval(new TestApi[F](testClient).routes.orNotFound.pure[F])
      //test <- testClient.getSomething("123")
      //_ = println(test)
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(routes)
        .serve
    } yield exitCode
  }
}
