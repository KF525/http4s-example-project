package config

import pureconfig.ConfigReader.Result
import pureconfig._
import pureconfig.generic.auto._

case class DatabaseConfig(driver: String,
                          url: String,
                          username: String,
                          password: String,
                          maximumPoolSize: Int,
                          minimumIdle: Int,
                          threadPoolSize: Int)

object DatabaseConfig {
  implicitly[ConfigReader[DatabaseConfig]]

  def load(): Result[DatabaseConfig] = ConfigSource.default.load[DatabaseConfig]

}