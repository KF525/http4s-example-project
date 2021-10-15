package zio

import cats.implicits.toSemigroupKOps
import config.{DatabaseConfig, ServiceConfig}
import database.Transaction
import doobie.hikari.HikariTransactor
import org.http4s.{HttpRoutes, Uri}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import pureconfig._
import zio.console.Console
import zio.http.{PoemApi, Routes}
import zio.interop.console.cats.putStrLn
import zio.interop.catz._
import pureconfig.{ConfigReader, loadConfig}
import zio.blocking.Blocking
import zio.client.{Http4sClient, PoemClient}
import zio.controller.PoemController
import zio.internal.Platform


object Main2 extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = startupApp.orDie.exitCode

  implicit val runtime: Runtime[ZEnv] = Runtime.default

  val startupApp: ZIO[Console, Throwable, Unit] =
    for {
      _ <- putStrLn("ZIO app starting")
      _ <- putStrLn("Loading Application Config")
      serviceConfig <- Task.effectTotal(ServiceConfig.load().getOrElse(throw new RuntimeException("Failed to load service config")))
      databaseConfig <- Task.effectTotal(DatabaseConfig.load().getOrElse(throw new RuntimeException("Failed to load database config")))
//      transactor: Managed[Throwable, HikariTransactor[Task]] = new Transaction().createTransactor(databaseConfig)
//      _ <- transactor.use {
//        t =>
//          Transaction.migrate(t)
//      }
      _ <- putStrLn("Building Service")
      httpClientResource = BlazeClientBuilder[Task](runtime.platform.executor.asEC)
        .withConnectTimeout(serviceConfig.connectTimeout)
        .withRequestTimeout(serviceConfig.requestTimeout)
        .resource.toManagedZIO
      _ <- httpClientResource.use(client => buildServer(client, serviceConfig))
    } yield ()

  private def buildServer(client: Client[Task], config: ServiceConfig): ZIO[Console, Throwable, Unit] = {
    for {
      _ <- putStrLn("Starting Blaze Server")
      http4sClient = new Http4sClient(client)
      poemClient = new PoemClient(http4sClient, Uri.unsafeFromString("https://poetrydb.org/"))
      controller = new PoemController(poemClient)
      routes: HttpRoutes[Task] = new Routes().routes <+> new PoemApi(controller).routes
      _ <- ZioHttp4sBlaze.runBlazeServer(routes, config.servicePort)
    } yield ()
  }
}
