package zio

import cats.data.Kleisli
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

//case class ZioHttp4sBlazeServer(routes: HttpRoutes[Task], port: Int) {
//  def runBlazeServer = {
//    import org.http4s.HttpRoutes
//
//    Task[Unit] = {
//      val app = buildHttpApp(routes)
//
//      implicit val runtime: Runtime[Any] = Runtime.default
//
//      BlazeServerBuilder[Task](ExecutionContext.global)
//        .bindHttp(port, host = "0.0.0.0")
//        .withHttpApp(app)
//        .serve
//        .compile
//        .drain
//    }
//
//    private def buildHttpApp(routes: HttpRoutes[Task]) =
//      Kleisli((a: Request[Task]) => routes.run(a).getOrElse(Response.notFound))
//  }
//}
