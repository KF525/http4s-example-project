package database

import cats.Applicative
import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import com.zaxxer.hikari._
import config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.api.output.MigrateResult
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
  def initialize[F[_] : Sync : Applicative](transactor: HikariTransactor[F]): Resource[F, F[Unit]] = {
    Resource.eval(
      Sync[F].delay {
        transactor.configure {
          dataSource => {
            println("DATASOURCE")
            val a: HikariDataSource = dataSource
            println(dataSource.getDriverClassName)
            println(dataSource.getJdbcUrl)
            val f: FluentConfiguration = Flyway.configure().dataSource(dataSource)
            val t = f.getDataSource
            println(t)
            println(t.getConnection)
            val x: Flyway = f.load
            val z: DataSource = x.getConfiguration.getDataSource
            x.migrate()
            Sync[F].delay(())
          }
        }
      }
    )
  }
}