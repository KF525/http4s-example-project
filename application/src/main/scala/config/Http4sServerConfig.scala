package config

import pureconfig.ConfigReader.Result
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig._
import pureconfig.generic.auto._

case class Http4sServerConfig( servicePort: Int)

object Http4sServerConfig {
  implicitly[ConfigReader[Http4sServerConfig]]

  def load(): Result[Http4sServerConfig] = ConfigSource.default.load[Http4sServerConfig]
}
