package ziohelpers

import cats.effect.Resource
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import zio.interop.catz._

import scala.concurrent.duration.Duration
import zio._


trait BlazeClientConfig {
  def connectTimeout: Duration
  def requestTimeout: Duration
}

object ZioHttp4sBlazeClient {

  implicit val runtime: Runtime[Any] = Runtime.default

  def blazeClientLayer: ZLayer[Has[BlazeClientConfig], Throwable, Has[Client[Task]]] = {
    for {
      config <- ZLayer.requires[Has[BlazeClientConfig]]
      client <- buildBlazeClient(config.get).toManagedZIO.toLayer
    } yield client
  }

  private def buildBlazeClient(config: BlazeClientConfig): Resource[Task, Client[Task]] =
    BlazeClientBuilder[Task](runtime.platform.executor.asEC)
      .withConnectTimeout(config.connectTimeout)
      .withRequestTimeout(config.requestTimeout)
      .resource

}