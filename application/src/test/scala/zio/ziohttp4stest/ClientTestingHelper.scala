package zio.ziohttp4stest

import org.http4s.client.Client
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.{HttpRoutes, Method, Response, Uri}
import zio.clock.Clock
import zio.duration._
import zio.interop.catz._
import zio.{Ref, Task, ZIO}

case class ObservedRequest(uri: Uri, method: Method)

object ClientTestingHelper {
  def withResponse[R, E, A](response: Task[Response[Task]], delay: Duration = 0.seconds)(f: Client[Task] => ZIO[R, E, A]): ZIO[R with Clock, E, (ObservedRequest, A)] =
    (for {
      requestRef <- Ref.make[ObservedRequest](null)
      actualResponse <- f {
        Client.fromHttpApp(HttpRoutes.of[Task] {
          case request =>
            for {
              actualRawRequestBody <- request.as[String]
              actualUri = request.uri
              actualContentType = request.contentType
              actualMethod = request.method
              _ <- requestRef.set(ObservedRequest(actualUri, actualMethod))
              result <- response
            } yield result
        }.orNotFound)
      }
      actualRequest <- requestRef.get
    } yield (actualRequest, actualResponse)).delay(delay)
}
