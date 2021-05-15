package database

import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import com.zaxxer.hikari.HikariConfig
import config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.flywaydb.core.Flyway
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import pureconfig.loadConfig

import scala.concurrent.duration.DurationInt

class Transaction[F[_]: Async : ContextShift](databaseConfig: DatabaseConfig) {

     /**  Returns a Resource yielding a transactor configured with a bounded connect EC and an unbounded transaction EC. The Transactor can transform ConnectionIO ~> IO, which gives us a program we can run. Specifically it gives us an IO that, when run, will connect to a database and execute a single transaction. Everything will be closed and shut down cleanly after use. */
  def createTransactor: Resource[F, HikariTransactor[F]] = {
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

object Transaction {

  def initialize[F[_]: Sync ](transactor: HikariTransactor[F]): F[Unit] = {
    transactor.configure { dataSource =>
      Sync[F].delay {
        println("Inside the sync")
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        println("About to migrate!")
        flyWay.migrate()
        println("Done migrating!")
        ()
      }
    }
  }

}