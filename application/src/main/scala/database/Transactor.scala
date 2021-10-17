package database

import cats.effect.Blocker
import com.typesafe.config.Config
import com.zaxxer.hikari.HikariDataSource
import config.DatabaseConfig
import doobie.Transactor
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway
import org.flywaydb.core.Flyway.configure
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.ClassicConfiguration
import zio.blocking.Blocking
import zio.interop.catz._
import zio.{Has, Managed, RIO, Task, URIO, ZIO, ZLayer, ZManaged, blocking}

import scala.concurrent.ExecutionContext

object DBTransactor {

    type DBTransactor = Has[Transactor[Task]]

    def makeTransactor(conf: DatabaseConfig, connectEC: ExecutionContext,
                       transactEC: ExecutionContext): Managed[Throwable, Transactor[Task]] =
      HikariTransactor
        .newHikariTransactor[Task](
          conf.driver,
          conf.url,
          conf.username,
          conf.password,
          connectEC,
          Blocker.liftExecutionContext(transactEC)
        ).toManagedZIO

  val managedTransaction: ZManaged[Blocking, Throwable, Transactor[Task]] =
    for {
      config <- Task.effectTotal(DatabaseConfig.load().getOrElse(throw new RuntimeException("Failed to load monix.database config"))).toManaged_
      connectEC <- ZIO.descriptor.map(_.executor.asEC).toManaged_
      blockingEC <- blocking.blocking(ZIO.descriptor.map(_.executor.asEC)).toManaged_
      transactor <- makeTransactor(config, connectEC, blockingEC)
    } yield transactor

  val managed: ZManaged[Has[DatabaseConfig] with Blocking, Throwable, Transactor[Task]] =
      for {
        config <- Task.effectTotal(DatabaseConfig.load().getOrElse(throw new RuntimeException("Failed to load monix.database config"))).toManaged_
        connectEC <- ZIO.descriptor.map(_.executor.asEC).toManaged_
        blockingEC <- blocking.blocking(ZIO.descriptor.map(_.executor.asEC)).toManaged_
        transactor <- makeTransactor(config, connectEC, blockingEC)
      } yield transactor

    val managedWithMigration: ZManaged[Has[DatabaseConfig] with Blocking, Throwable, Transactor[Task]] =
      Migration.migrate.toManaged_ *> managed

    val test: ZLayer[Has[DatabaseConfig] with Blocking, Throwable, DBTransactor] =
      ZLayer.fromManaged(managed)

    val live: ZLayer[Has[DatabaseConfig] with Blocking, Throwable, DBTransactor] =
      ZLayer.fromManaged(managedWithMigration)

    val transactor: URIO[DBTransactor, Transactor[Task]] = ZIO.service
}

object Migration {

  private val cpLocation = new Location("classpath:db/migration")
  private val fsLocation = new Location("filesystem:db/migration")
  //DBTransactor.configure(dataSource => loadFlyWayAndMigrate(dataSource))
  //dataSource: HikariDataSource
  val migrate: RIO[Has[DatabaseConfig], Unit] =
    Task.effectTotal(DatabaseConfig.load().getOrElse(throw new RuntimeException("Failed to load monix.database config")))
      .flatMap { cfg =>
        ZIO.effect {
          Flyway.configure()
          //.dataSource()
          //.load()
          //.migrate()
          val config = new ClassicConfiguration()
          config.setDataSource(cfg.url, cfg.username, cfg.password)
          config.setLocations(cpLocation, fsLocation)
          val newFlyway = new Flyway(config)
          newFlyway.migrate()
        }.unit
        // }
      }
      //.tapError(err => ZIO.fail(s"Error migrating monix.database: $err.")))
}

