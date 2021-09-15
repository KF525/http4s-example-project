package config

import pureconfig._
import pureconfig.generic.auto._

case class DatabaseConfig(driver: String,
                          url: String,
                          username: String,
                          password: String,
                          maximumPoolSize: Int,
                          minimumIdle: Int,
                          threadPoolSize: Int)