package store

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import cats.free.Free
import doobie.free.connection
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import model.User
import doobie.implicits._
import cats.implicits._

class UserStore[F[_]: Sync: ConcurrentEffect : Timer : ContextShift ](transactor: Transactor[F]) {

  def create(user: User): F[User] = {
    val plan: Free[connection.ConnectionOp, Unit] = for {
     _ <-  createQuery(user).run
    } yield ()

    println(plan)
    println(transactor)
    plan.transact(transactor).map(_ => user)
  }

  private def createQuery(user: User): Update0 = {
    println(user)
    val User(first_name, last_name, email) = user
    val x = sql"""insert into poem_user
         |(first_name, last_name, email)
         |values
         |($first_name, $last_name, $email)
         |""".update
    println(x)
    x
  }

}