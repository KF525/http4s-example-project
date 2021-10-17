package config

import pureconfig.ConfigReader.Result
import pureconfig._
import pureconfig.generic.auto._
import scala.concurrent.duration.Duration

case class ServiceConfig(serviceHost: String,
                         servicePort: Int,
                         connectTimeout: Duration,
                         requestTimeout: Duration,
                         retryAttempts: Int,
                         backoffIntervalMs: Int,
                         timeoutPerAttemptMs: Int)

object ServiceConfig {
  implicitly[ConfigReader[ServiceConfig]]

  def load(): Result[ServiceConfig] = ConfigSource.default.load[ServiceConfig]
}