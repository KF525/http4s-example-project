import cats.{Applicative, Monad}
import cats.effect.{ConcurrentEffect, ContextShift, Resource, Sync, Timer}
import client.PoemClient
import config.{DatabaseConfig, ServiceConfig}
import database.Transaction
import doobie.Transactor
import http.{CompoundPoemApi, PoemApi, UserApi}
import org.http4s.{HttpRoutes, Uri}
import monix.execution.Scheduler.Implicits.global
import store.{CompoundPoemStore, UserStore}
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import pureconfig.{ConfigReader, loadConfig}
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server._
import cats.implicits._
import controller.UserController
import org.http4s.implicits._

import scala.concurrent.duration.DurationInt

object Server {

  def stream[F[_] : ConcurrentEffect : Timer : ContextShift : Sync : Applicative]: Resource[F, Server[F]] = {
    val serviceConfig: ServiceConfig = ConfigSource.default.loadOrThrow[ServiceConfig]
    val databaseConfig: DatabaseConfig = ConfigSource.default.loadOrThrow[DatabaseConfig]

    for {
      client <- BlazeClientBuilder[F](global)
        .withConnectTimeout(serviceConfig.connectTimeout.seconds)
        .withRequestTimeout(serviceConfig.requestTimeout.seconds).resource
      poemClient = new PoemClient[F](client, Uri.unsafeFromString("https://poetrydb.org/"))
      database <- new Transaction[F](databaseConfig).createTransactor
      //_ = Transaction.initialize(database)
      server <- buildService(serviceConfig, poemClient, database)
    } yield server
  }

  def buildService[F[_] : ConcurrentEffect : Timer : ContextShift : Monad]
  (serviceConfig: ServiceConfig, client: PoemClient[F], database: Transactor[F]): Resource[F, Server[F]] =
    buildServer(serviceConfig, buildRoutes(client, database))

  /**
   * Returns a kleisli with a Request input and a Response output, such that the response effect is an optional inside the effect of the request and response bodies. HTTP routes can conveniently be constructed from a partial function and combined as a SemigroupK.
   * Type parameters:
   * F – the effect type of the Request and Response bodies, and the base monad of the OptionT in which the response is returned.
   * https://typelevel.org/cats/datatypes/kleisli.html
   *
   * The central concept of http4s-dsl is pattern matching. An HttpRoutes[F] is declared as a simple series of case statements. Each case statement attempts to match and optionally extract from an incoming Request[F]. The code associated with the first matching case is used to generate a F[Response[F]].
   *
   * Response[F] hasn’t been created yet. We wrapped it in a monadic type [F]. In a real service, generating a Response[F] is likely to be an asynchronous operation with side effects, such as invoking another web service or querying a database, or maybe both. Operating in a F gives us control over the sequencing of operations and lets us reason about our code like good functional programmers. It is the HttpRoutes[F]’s job to describe the task, and the server’s job to run it.
   */
  def buildRoutes[F[_] : ConcurrentEffect : Timer : ContextShift]
  (client: PoemClient[F], database: Transactor[F]): HttpRoutes[F] = {
    val poemApi: HttpRoutes[F] = new PoemApi[F](client).routes
    val compoundPoemApi: HttpRoutes[F] = new CompoundPoemApi[F](new CompoundPoemStore[F](database)).routes
    val userApi: HttpRoutes[F] = new UserApi[F](
      new UserController[F](new UserStore[F](database))).routes
    poemApi <+> compoundPoemApi <+> userApi
  }

  def buildServer[F[_] : ConcurrentEffect : Timer : ContextShift]
  (serviceConfig: ServiceConfig, routes: HttpRoutes[F]): Resource[F, Server[F]] = {
    val httpApp = Router("/" -> routes).orNotFound
    BlazeServerBuilder[F](global)
      .withBanner(List(s"Http4s Server started successfully on port: ${serviceConfig.servicePort}"))
      .bindHttp(serviceConfig.servicePort, serviceConfig.serviceHost).withHttpApp(httpApp).resource
  }
}