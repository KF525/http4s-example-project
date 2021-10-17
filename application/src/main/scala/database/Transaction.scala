package database

import cats.effect.{Blocker, Resource}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import config.DatabaseConfig
import doobie.Transactor
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import zio.blocking.Blocking
import zio.console.Console
import zio.{Managed, Task, ZIO}
import zio.interop.catz._
import zio.interop.console.cats.putStrLn

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class Transaction {

  def createTransactor(databaseConfig: DatabaseConfig)
                      (implicit rt: zio.Runtime[Blocking]): Managed[Throwable, Transactor[Task]] = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setDriverClassName("org.postgresql.Driver")
    hikariConfig.setJdbcUrl(databaseConfig.url)
    hikariConfig.setUsername(databaseConfig.username)
    hikariConfig.setPassword(databaseConfig.password)
    hikariConfig.setMaxLifetime(345.seconds.toMillis)
    hikariConfig.setMaximumPoolSize(databaseConfig.maximumPoolSize)
    hikariConfig.setMinimumIdle(databaseConfig.minimumIdle)

    val transactor: Resource[Task, Transactor[Task]] = for {
      connectEC <- ExecutionContexts.fixedThreadPool[Task](2)
      transactEC <- ExecutionContexts.cachedThreadPool[Task]
      xa <- HikariTransactor.fromHikariConfig[Task](
        hikariConfig,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      )
    } yield xa

    transactor.toManagedZIO
  }
}

object Transaction {
  def migrate(dbTransactor: HikariTransactor[Task]): ZIO[Console, Throwable, Unit] = for {
    _ <- putStrLn("Starting Flyway migration")
    _ <- dbTransactor.configure(dataSource => loadFlyWayAndMigrate(dataSource))
    _ <- putStrLn("Finished Flyway migration")
  } yield ()

  private def loadFlyWayAndMigrate(dataSource: HikariDataSource): Task[MigrateResult] =
    ZIO.effect {
      Flyway.configure()
        .dataSource(dataSource)
        .load()
        .migrate()
  }

//  def migrate(dbTransactor: HikariTransactor[Task]): ZIO[FlywayMigrator with Console, Throwable, Unit] =
//    ZIO.accessM[FlywayMigrator with Console](_.flywayMigrator.migrate(dbTransactor))
}
