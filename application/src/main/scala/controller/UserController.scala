//package controller
//
//import cats.data.EitherT
//import cats.effect.Sync
//import error.CompoundPoemError
//import model.{Email, User}
//import model.request.CreateUserRequest
//import store.UserStore
//
//class UserController[F[_]: Sync](userStore: UserStore[F]) {
//
//  def create(request: CreateUserRequest): F[User] =
//    userStore.create(User(request.firstName, request.lastName, Email(request.email)))
//
////  def create(request: CreateUserRequest): EitherT[F, CompoundPoemError, User] =
////    EitherT.liftF(userStore.create(User(request.firstName, request.lastName, Email(request.email))))
//
//  def get(userId: Int): F[Option[User]] = userStore.get(userId)
//}
