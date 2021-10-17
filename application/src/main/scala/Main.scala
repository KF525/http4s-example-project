import cats.implicits.toSemigroupKOps
import client.{Http4sClient, PoemClient}
import config.{DatabaseConfig, ServiceConfig}
import controller.{CompoundPoemController, PoemController}
import database.Transaction
import doobie.Transactor
import http.{CompoundPoemApi, PoemApi, Routes}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.{HttpRoutes, Uri}
import store.CompoundPoemStore
import zio.clock.Clock
import zio.console.Console
import zio.duration.durationInt
import zio.interop.catz._
import zio.interop.console.cats.putStrLn
import zio.{ExitCode, Managed, Runtime, Task, URIO, ZEnv, ZIO}

object Main extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = startupApp.orDie.exitCode

  implicit val runtime: Runtime[ZEnv] = Runtime.default

  val startupApp: ZIO[Clock with Console, Throwable, Unit] =
    for {
      _ <- putStrLn("ZIO app starting")
      _ <- putStrLn("Loading Application Config")
      serviceConfig <- Task.effectTotal(ServiceConfig.load().getOrElse(throw new RuntimeException("Failed to load service config")))
      databaseConfig <- Task.effectTotal(DatabaseConfig.load().getOrElse(throw new RuntimeException("Failed to load monix.database config")))
      transactor: Managed[Throwable, Transactor[Task]] = new Transaction().createTransactor(databaseConfig)
      httpClientResource: Managed[Throwable, Client[Task]] = BlazeClientBuilder[Task](runtime.platform.executor.asEC)
        .withConnectTimeout(serviceConfig.connectTimeout)
        .withRequestTimeout(serviceConfig.requestTimeout)
        .resource.toManagedZIO
      combined = combineResources(transactor, httpClientResource)
      _ <- putStrLn("Building Service")
      _ <- combined.use { case (transactor, client) => buildServer(transactor, client, serviceConfig) }
    } yield ()

  private def combineResources(managedTransactor: Managed[Throwable, Transactor[Task]],
                               managedClient: Managed[Throwable, Client[Task]]): Managed[Throwable, (Transactor[Task], Client[Task])] =
    for {
      transactor <- managedTransactor
      client <- managedClient
    } yield (transactor, client)

  private def buildServer(transactor: Transactor[Task],
                          client: Client[Task],
                          config: ServiceConfig): ZIO[Clock with Console, Throwable, Unit] = {
    for {
      _ <- putStrLn("Starting Blaze Server")
      clock <- ZIO.environment[Clock]
      console <- ZIO.environment[Console]
      http4sClient = new Http4sClient(client)
      poemClient = new PoemClient(http4sClient, Uri.unsafeFromString("https://poetrydb.org/"),
        config.retryAttempts, config.backoffIntervalMs.millis, config.timeoutPerAttemptMs.millis)
      poemController = new PoemController(poemClient, clock, console)
      compoundPoemStore = new CompoundPoemStore(transactor)
      compoundPoemController = new CompoundPoemController(compoundPoemStore)
      routes: HttpRoutes[Task] = new Routes().routes <+> new PoemApi(poemController).routes <+> new CompoundPoemApi(compoundPoemController).routes
      _ <- ZioHttp4sBlaze.runBlazeServer(routes, config.servicePort)
    } yield ()
  }
}