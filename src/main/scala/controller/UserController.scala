package controller

import cats.effect.Sync
import model.{CreateUserRequest, User}
import store.UserStore

class UserController[F[_]: Sync](userStore: UserStore[F]) {

  def create(request: CreateUserRequest): F[User] = {
    userStore.create(User(request.firstName, request.lastName, request.email))
  }
}
