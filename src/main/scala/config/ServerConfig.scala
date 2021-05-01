package config

import pureconfig._
import pureconfig.generic.auto._

case class ServerConfig(host: String, port: Int)