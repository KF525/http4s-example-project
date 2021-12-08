package ziohelpers

import cats.data.Kleisli
import config.Http4sServerConfig
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.server.blaze.BlazeServerBuilder
import zio._
import zio.interop.catz.implicits._
import zio.interop.catz._

import scala.concurrent.ExecutionContext

//trait Http4sServerConfig {
//  def servicePort: Int
//}

case class ZioHttp4sBlazeServer(routes: HttpRoutes[Task], config: Http4sServerConfig) {
  def runBlazeServer = {
    import org.http4s.HttpRoutes

    def runBlazeServer: Task[Unit] = {
      val app = buildHttpApp(routes)

      implicit val runtime: Runtime[Any] = Runtime.default

      BlazeServerBuilder[Task](ExecutionContext.global)
        .bindHttp(config.servicePort, host = "0.0.0.0")
        .withHttpApp(app)
        .serve
        .compile
        .drain
    }

    def buildHttpApp(routes: HttpRoutes[Task]) =
      Kleisli((a: Request[Task]) => routes.run(a).getOrElse(Response.notFound))
  }
}

object ZioHttp4sBlazeServer {
  def layer: URLayer[Has[HttpRoutes[Task]] with Has[Http4sServerConfig], Has[ZioHttp4sBlazeServer]]
 = (ZioHttp4sBlazeServer.apply _).toLayer}
