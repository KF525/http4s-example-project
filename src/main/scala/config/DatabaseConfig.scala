package config

import pureconfig._
import pureconfig.generic.auto._

case class DatabaseConfig(driver: String,
                          url: String,
                          username: String,
                          password: String,
                          maximumPoolSize: 10,
                          minimumIdle: 3,
                          threadPoolSize: 5)