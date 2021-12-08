package config

import pureconfig.ConfigReader.Result
import pureconfig._
import pureconfig.generic.auto._
import scala.concurrent.duration.Duration

case class GeneralConfig(serviceHost: String,
                         servicePort: Int,
                         connectTimeout: Duration,
                         requestTimeout: Duration,
                         retryAttempts: Int,
                         backoffIntervalMs: Int,
                         timeoutPerAttemptMs: Int)

object GeneralConfig {
  implicitly[ConfigReader[GeneralConfig]]

  def load(): Result[GeneralConfig] = ConfigSource.default.load[GeneralConfig]
}