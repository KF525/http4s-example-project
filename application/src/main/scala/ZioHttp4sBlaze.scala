import cats.data.Kleisli
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{HttpRoutes, Request, Response}
import zio.{Runtime, Task}
import zio.interop.catz.implicits._
import zio.interop.catz._
import scala.concurrent.ExecutionContext

object ZioHttp4sBlaze {

  def runBlazeServer(routes: HttpRoutes[Task], port: Int): Task[Unit] = {
    val app = buildHttpApp(routes)

    implicit val runtime: Runtime[Any] = Runtime.default

    BlazeServerBuilder[Task](ExecutionContext.global)
      .bindHttp(port, host = "0.0.0.0")
      .withHttpApp(app)
      .serve
      .compile
      .drain
  }

  private def buildHttpApp(routes: HttpRoutes[Task]) =
    Kleisli((a: Request[Task]) => routes.run(a).getOrElse(Response.notFound))
}
