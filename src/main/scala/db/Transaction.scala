package db

import cats.effect.{Async, Blocker, ContextShift, IO, Resource}
import com.zaxxer.hikari.HikariConfig
import config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import pureconfig.loadConfig

import scala.concurrent.duration.DurationInt

class Transaction[F[_]: Async : ContextShift](config: DatabaseConfig) {

  // We need a ContextShift[IO] before we can construct a Transactor[IO]. The passed ExecutionContext
  // is where nonblocking operations will be executed. For testing here we're using a synchronous EC.
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  // A transactor that gets connections from java.sql.DriverManager and executes blocking operations
  // on an our synchronous EC. See the chapter on connection handling for more info.
  /**
   * A Transactor is a data type that knows how to connect to a database, hand out connections, and clean them up; and with this knowledge it can transform ConnectionIO ~> IO, which gives us a program we can run. Specifically it gives us an IO that, when run, will connect to the database and execute single transaction.
   */
  def mxa: Transactor[F] = Transactor.fromDriverManager[F](
    config.driver,
    config.url,
    config.username,
    config.password
  )

  //To replace the current transactor---
  def createTransactor(databaseConfig: DatabaseConfig): Resource[F, HikariTransactor[F]] = {
    // Resource yielding a transactor configured with a bounded connect EC and an unbounded
    // transaction EC. Everything will be closed and shut down cleanly after use.
    val hikariConfig = new HikariConfig()
    hikariConfig.setDriverClassName("org.postgresql.Driver")
    hikariConfig.setJdbcUrl(databaseConfig.url)
    hikariConfig.setUsername(databaseConfig.username)
    hikariConfig.setPassword(databaseConfig.password)
    hikariConfig.setMaxLifetime(345.seconds.toMillis)
    hikariConfig.setMaximumPoolSize(databaseConfig.maximumPoolSize)
    hikariConfig.setMinimumIdle(databaseConfig.minimumIdle)

    val transactor: Resource[F, HikariTransactor[F]] = {
      for {
        ce <- ExecutionContexts.fixedThreadPool[F](databaseConfig.threadPoolSize) // our connect EC
        be <- ExecutionContexts.cachedThreadPool[F] // our blocking EC
        xa <- HikariTransactor.fromHikariConfig[F](
          hikariConfig,
          connectEC = ce, // await connection here
          Blocker.liftExecutionContext(be) // execute JDBC operations here
        )
      } yield xa
    }
    transactor
  }
}