import cats.implicits.toSemigroupKOps
import client.{Http4sThinClient, PromptClient}
import config.{DatabaseConfig, ServiceConfig}
import controller.{CompoundPoemController, PromptController}
import database.Transaction
import doobie.hikari.HikariTransactor
import http.{CompoundPoemApi, PromptApi, Routes}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.{HttpRoutes, Uri}
import store.CompoundPoemStore
import zio.clock.Clock
import zio.console.Console
import zio.duration.durationInt
import zio.interop.console.cats.putStrLn
import zio.{ExitCode, Managed, Runtime, Task, URIO, ZEnv, ZIO}
import zio.interop.catz._

object Main extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = startupApp.orDie.exitCode

  implicit val runtime: Runtime[ZEnv] = Runtime.default

  val startupApp: ZIO[Clock with Console, Throwable, Unit] =
    for {
      _ <- putStrLn("ZIO app starting")
      _ <- putStrLn("Loading Application Config")
      serviceConfig <- Task.effectTotal(ServiceConfig.load().getOrElse(throw new RuntimeException("Failed to load service config")))
      databaseConfig <- Task.effectTotal(DatabaseConfig.load().getOrElse(throw new RuntimeException("Failed to load monix.database config")))
      transactor: Managed[Throwable, HikariTransactor[Task]] = new Transaction().createTransactor(databaseConfig)
      httpClientResource: Managed[Throwable, Client[Task]] = BlazeClientBuilder[Task](runtime.platform.executor.asEC)
        .withConnectTimeout(serviceConfig.connectTimeout)
        .withRequestTimeout(serviceConfig.requestTimeout)
        .resource.toManagedZIO
      combined = combineResources(transactor, httpClientResource)
      _ <- putStrLn("Building Service")
      _ <- combined.use { case (transactor, client) => buildServer(transactor, client, serviceConfig) }
    } yield ()

  private def combineResources(managedTransactor: Managed[Throwable, HikariTransactor[Task]],
                               managedClient: Managed[Throwable, Client[Task]]): Managed[Throwable, (HikariTransactor[Task], Client[Task])] =
    for {
      transactor <- managedTransactor
      client <- managedClient
    } yield (transactor, client)

  private def buildServer(transactor: HikariTransactor[Task],
                          client: Client[Task],
                          config: ServiceConfig): ZIO[Clock with Console, Throwable, Unit] = {
    for {
      clock <- ZIO.environment[Clock]
      console <- ZIO.environment[Console]
      _ <- transactor.configure(dataSource => Transaction.loadFlyWayAndMigrate(dataSource))
      poemClient = new PromptClient(Http4sThinClient(client), Uri.unsafeFromString("https://poetrydb.org/"),
        config.retryAttempts, config.backoffIntervalMs.millis, config.timeoutPerAttemptMs.millis)
      poemController = new PromptController(poemClient, clock, console)
      compoundPoemStore = new CompoundPoemStore(transactor)
      compoundPoemController = new CompoundPoemController(compoundPoemStore)
      routes: HttpRoutes[Task] = Routes().routes <+> PromptApi(poemController).routes <+> CompoundPoemApi(compoundPoemController).routes
      _ <- putStrLn("Starting Blaze Server")
      _ <- ZioHttp4sBlaze.runBlazeServer(routes, config.servicePort)
    } yield ()
  }
}