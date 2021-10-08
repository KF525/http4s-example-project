package util

import org.http4s._
import org.http4s.client.Client
import org.scalatest.matchers.should.Matchers
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import monix.eval.Task
import monix.execution.Scheduler
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Http4sTestClient(service: HttpRoutes[Task]) extends Matchers {

  private val httpApp = service.orNotFound
  private val client = Client fromHttpApp httpApp

  def expect[A](request: Task[Request[Task]])(implicit e: EntityDecoder[Task, A], scheduler: Scheduler): (Status, A) = {
    futureValue(request.flatMap(client.run(_).use(response => extractStatusAndBody(response))))
  }

  private def extractStatusAndBody[A](response: Response[Task])(implicit e: EntityDecoder[Task, A]): Task[(Status, A)] = {
    val x: Task[(Status, A)] = response.as[A].map(value => {
      (response.status, value)
    })
    x
  }

  private def futureValue[A](response: Task[(Status, A)])(implicit scheduler: Scheduler): (Status, A) =
    Await.result(response.runToFuture, Duration.fromNanos(1000L))
}