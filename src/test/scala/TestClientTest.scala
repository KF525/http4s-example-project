import monix.catnap.MVar
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.http4s.headers.{Accept, MediaRangeAndQValue}

class TestClientTest extends AnyFlatSpec with should.Matchers with Http4sDsl[Task] {

  "TestClient" should "GET from path" in {
    val baseUri = Uri.unsafeFromString("https://www.baseuri.com")
    val (request, _) = futureValue(withResponse(Ok()){client => new TestClient[Task](client, baseUri).getSomething("ca") })

    request.method should be(Method.GET)
    request.headers.get(Accept) should be(Some(Accept(MediaRangeAndQValue(MediaRange.`text/*`))))
    request.uri should be(Uri.unsafeFromString(s"$baseUri/states/ca/current.json"))
  }

  private def futureValue[A](request: Task[(Request[Task], A)]): (Request[Task], A) =
    Await.result(request.runToFuture, Duration.fromNanos(1000L))

  private def withResponse[A](response: Task[Response[Task]])(f: Client[Task] => Task[A]): Task[(Request[Task], A)] =
    for {
      requestVar <- MVar.empty[Task, Request[Task]]()
      actualResponse <- f {
        Client fromHttpApp HttpRoutes.of[Task] {
          case req =>
            for {
              _ <- requestVar.put(req)
              result <- response
            } yield result
        }.orNotFound
      }
      request <- requestVar.read
    } yield (request, actualResponse)
}
