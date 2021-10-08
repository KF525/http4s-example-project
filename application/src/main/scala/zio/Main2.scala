package zio

import config.{DatabaseConfig, ServiceConfig}
import database.Transaction
import doobie.hikari.HikariTransactor
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import pureconfig._
import zio.console.Console
import zio.http.Routes
import zio.interop.console.cats.putStrLn
import zio.interop.catz._
import pureconfig.{ConfigReader, loadConfig}
import zio.blocking.Blocking
import zio.client.PoemClient
import zio.internal.Platform


object Main2 extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = startupApp.orDie.exitCode

  implicit val runtime: Runtime[Any] = Runtime.default
  implicit val blocking: Runtime[Blocking] = Runtime.default.map(ZEnv.live)

  val startupApp: ZIO[Console, Throwable, Unit] =
    for {
      _ <- putStrLn("ZIO app starting")
      _ <- putStrLn("Loading Application Config")
      serviceConfig <- Task.effectTotal(ServiceConfig.load().getOrElse(throw new RuntimeException("Failed to load service config")))
      databaseConfig <- Task.effectTotal(DatabaseConfig.load().getOrElse(throw new RuntimeException("Failed to load database config")))
      transactor = new Transaction().createTransactor(databaseConfig)
      //_ <- transactor.use(t => Transaction.initialize(t)) --I might need to rewrite this more ZIO specifically.
      _ <- putStrLn("Building Service")
      httpClientResource = BlazeClientBuilder[Task](runtime.platform.executor.asEC)
        .withConnectTimeout(serviceConfig.connectTimeout)
        .withRequestTimeout(serviceConfig.requestTimeout)
        .resource.toManagedZIO
      _ <- httpClientResource.use(client => buildServer(client, serviceConfig))
    } yield ()

  private def buildServer(httpClient: Client[Task], config: ServiceConfig): ZIO[Console, Throwable, Unit] = {
    for {
      _ <- putStrLn("Starting Blaze Server")
      //client
      poemClient = new PoemClient(httpClient, Uri.unsafeFromString("https://poetrydb.org/"))
      _ <- ZioHttp4sBlaze.runBlazeServer(new Routes().routes, config.servicePort)
    } yield ()
  }
}
