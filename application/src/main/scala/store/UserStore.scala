//package store
//
//import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
//import cats.free.Free
//import doobie.free.connection
//import doobie.util.transactor.Transactor
//import doobie.util.update.Update0
//import model.{Email, User}
//import doobie.implicits._
//import cats.implicits._
//import doobie.util.fragment.Fragment
//import doobie.util.query.Query0
//
//class UserStore[F[_]: Sync: ConcurrentEffect : Timer : ContextShift ](transactor: Transactor[F]) {
//
//  def create(user: User): F[User] = {
//    val plan: Free[connection.ConnectionOp, Unit] = for {
//     _ <-  createUser(user).run
//    } yield ()
//
//    plan.transact(transactor).map(_ => user)
//  }
//
//  def get(userId: Int): F[Option[User]] = {
//    val plan = for {
//      row <- populateUser(getUser(userId))
//    } yield row
//
//    plan.option.transact(transactor)
//  }
//
//  private def populateUser(fragment: Fragment): Query0[User] = fragment.query[(String, String, String)].map {
//    case (firstName, lastName, email) => User(firstName, lastName, Email.parse(email))
//  }
//
//  private def getUser(userId: Int): Fragment =
//    fr"select first_name, last_name, email from poem_user where id = $userId"
//
//  private def createUser(user: User): Update0 = {
//    val User(first_name, last_name, Email(email)) = user
//    sql"insert into poem_user (first_name, last_name, email) values ($first_name, $last_name, $email)".update
//  }
//
//}