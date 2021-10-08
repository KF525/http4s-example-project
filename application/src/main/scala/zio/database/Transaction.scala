package zio.database

import cats.effect.{Blocker, Resource}
import com.zaxxer.hikari.HikariDataSource
import config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import zio.blocking.Blocking
import zio.console.Console
import zio.{Managed, Task, ZIO}
import zio.interop.catz._
import zio.interop.console.cats.putStrLn

class Transaction {

  def createTransactor(databaseConfig: DatabaseConfig)
                      (implicit rt: zio.Runtime[Blocking]): Managed[Throwable, HikariTransactor[Task]] = {
    val res: Resource[Task, HikariTransactor[Task]] = for {
      connectEC  <- ExecutionContexts.fixedThreadPool[Task](2)
      transactEC <- ExecutionContexts.cachedThreadPool[Task]
      xa <- HikariTransactor.newHikariTransactor[Task](
        databaseConfig.driver,
        databaseConfig.url,
        databaseConfig.username,
        databaseConfig.password,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      )
    } yield xa

    res.toManaged
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
