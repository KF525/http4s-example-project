package ziotestsyntax

import cats.data.NonEmptyList
import org.mockito.stubbing.OngoingStubbing
import zio.{Ref, ZEnv, ZIO}

object ZioTestSyntax {
  implicit class ZioTestHelper[E, A](z: ZIO[ZEnv, E, A]) {
    def unsafeRun: A = zio.Runtime.default.unsafeRun(z)

    def runFailure: E = {
      val failedZio = z.foldM(
        err => ZIO.succeed(err),
        _ => ZIO.fail(new RuntimeException("Expected error but didn't get one"))
      )
      zio.Runtime.default.unsafeRun(failedZio)
    }
  }

  implicit class OngoingZioStubbingHelper[R, E, A](stub: OngoingStubbing[ZIO[R, E, A]]) {
    def thenSucceed(value: A): OngoingStubbing[ZIO[R, E, A]] = stub thenReturn ZIO.effectTotal(value)

    def thenFail(error: E): OngoingStubbing[ZIO[R, E, A]] = stub thenReturn ZIO.fail(error)
  }

  implicit class OngoingZioStubbingHelperU[R, A](stub: OngoingStubbing[ZIO[R, Nothing, A]]) {
    def thenSucceed(value: A): OngoingStubbing[ZIO[R, Nothing, A]] = stub thenReturn ZIO.effectTotal(value)
  }

  def consecutively[R, E, A](ziosRef: Ref[NonEmptyList[ZIO[R, E, A]]]) =
    for {
      zios <- ziosRef.get
      result <- zios match {
        case NonEmptyList(head, tail) =>
          for {
            _ <- tail match {
              case Nil => ziosRef.set(NonEmptyList(head, tail))
              case h :: t => ziosRef.set(NonEmptyList(h, t))
            }
            r <- head
          } yield r
      }
    } yield result
}
