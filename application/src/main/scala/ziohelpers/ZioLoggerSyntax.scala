package ziohelpers

import com.typesafe.scalalogging.Logger
import zio.{UIO, ZIO}

object ZioLoggerSyntax {
  implicit class LoggerSyntaxExtension(logger: Logger) {
    def debugZ(msg: String): UIO[Unit] = ZIO.effect(logger.debug(msg)).ignore
    def infoZ(msg: String): UIO[Unit] = ZIO.effect(logger.info(msg)).ignore
    def warnZ(msg: String): UIO[Unit] = ZIO.effect(logger.warn(msg)).ignore
    def errorZ(msg: String): UIO[Unit] = ZIO.effect(logger.error(msg)).ignore
    def errorZ(msg: String, cause: Throwable): UIO[Unit] = ZIO.effect(logger.error(msg, cause)).ignore
  }
}