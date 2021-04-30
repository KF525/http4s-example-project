package postgres

import cats.effect.{Async, ContextShift, IO}
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

class Transaction[F[_]: Async : ContextShift] {

  // We need a ContextShift[IO] before we can construct a Transactor[IO]. The passed ExecutionContext
  // is where nonblocking operations will be executed. For testing here we're using a synchronous EC.
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  // A transactor that gets connections from java.sql.DriverManager and executes blocking operations
  // on an our synchronous EC. See the chapter on connection handling for more info.
  /**
   * A Transactor is a data type that knows how to connect to a database, hand out connections, and clean them up; and with this knowledge it can transform ConnectionIO ~> IO, which gives us a program we can run. Specifically it gives us an IO that, when run, will connect to the database and execute single transaction.
   */
  val mxa: Transactor[F] = Transactor.fromDriverManager[F](
    "org.postgresql.Driver",
    "jdbc:postgresql:test_db",
    "root",
    "unicorn"
  )
}