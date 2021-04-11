import cats.effect.{ConcurrentEffect, ExitCode, Timer}
import fs2.Stream
import org.http4s.Uri
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import monix.execution.Scheduler.Implicits.global

object Server {
  def stream[Task[_]: ConcurrentEffect: Timer]: Stream[Task, ExitCode] = {
    for {
      client <- BlazeClientBuilder[Task](global).stream
      testClient = new TestClient(client, Uri(path = "https://www.gutenberg.org"))
      routes = new TestApi[Task](testClient).routes.orNotFound
      exitCode <- BlazeServerBuilder[Task](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(routes)
        .serve
    } yield exitCode
  }
}
