package config

import pureconfig._
import pureconfig.generic.auto._

case class ServiceConfig(serviceHost: String,
                         servicePort: Int,
                         connectTimeout: Int,
                         requestTimeout: Int)