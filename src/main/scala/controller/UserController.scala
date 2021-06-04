package controller

import cats.data.EitherT
import cats.effect.Sync
import model.{Email, User}
import model.request.CreateUserRequest
import store.UserStore

class UserController[F[_]: Sync](userStore: UserStore[F]) {
  /**
   * EitherT[F[_], A, B] is a lightweight wrapper for F[Either[A, B]] that makes it easy to compose Eithers and Fs together. To use EitherT, values of Either, F, A, and B are first converted into EitherT, and the resulting EitherT values are then composed using combinators.
   */
  def create(request: CreateUserRequest): F[User] = {
    userStore.create(User(request.firstName, request.lastName, Email(request.email)))
  }
}
