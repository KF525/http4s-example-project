package zio.ziotestsyntax

import org.mockito.stubbing.OngoingStubbing
import zio.{Ref, ZEnv, ZIO}

object ZioTestSyntax {
  implicit class ZioTestHelper[E, A](z: ZIO[ZEnv, E, A]) {
    def unsafeRun: A = zio.Runtime.default.unsafeRun(z)

    def runFailure: E = {
      val failedZio = z.either.flatMap {
        case Left(err) => ZIO.succeed(err)
        case Right(_) => ZIO.fail(new RuntimeException("Expected monix.error but didn't get one"))
      }
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

  def consecutively[R, E, A](ziosRef: Ref[List[ZIO[R, E, A]]]): ZIO[R, E, Option[A]] =
    for {
      zios <- ziosRef.get
      result <- zios match {
        case Nil =>
          ZIO.succeed(None)
        case head :: remainder =>
          for {
            _ <- ziosRef.set(remainder)
            r <- head
          } yield Some(r)
      }
    } yield result

  //  it should "learn about zio schedule" in {
  //    trait NotifierTimeoutError
  //    case object GlobalTimeoutError extends NotifierTimeoutError
  //    case object ZioTimeoutError extends NotifierTimeoutError
  //
  //    //run 3 times in a row with fixed space of 500 millis between, then will stop
  //    //onDecision tracks the number of attempts (out), not sure this is exactly what we want...???
  //    val schedule2: Schedule[Any, Any, (Duration, Long)] = Schedule.linear(50.millis) && Schedule.recurs(5)//.onDecision({
  //     // case zio.Schedule.Decision.Done(out) => out)
  //      //case zio.Schedule.Decision.Continue(out, _, _) => out)
  //    //})
  //
  //    //timeoutFail: The same as timeout, but instead of producing a None in the event of timeout, it will produce the specified monix.error.
  //    val tasks2: List[ZIO[Console with Clock, NotifierTimeoutError, Unit]] = List(
  //      putStrLn("a").delay(6.second).timeoutFail(ZioTimeoutError)(5.seconds),
  //      putStrLn("b").delay(6.seconds).timeoutFail(ZioTimeoutError)(5.seconds),
  //      putStrLn("c").delay(8.second).timeoutFail(ZioTimeoutError)(5.seconds),
  //      putStrLn("d").delay(1.second).timeoutFail(ZioTimeoutError)(5.seconds),
  //      putStrLn("e").delay(1.second).timeoutFail(ZioTimeoutError)(5.seconds),
  //    )
  //
  //    //TODO: retryOrElseEither
  //    val work2 = for {
  //      refConsecutiveTasks <- Ref.make(tasks2)
  //      finalResult <- consecutively(refConsecutiveTasks).retry(schedule2).timeoutFail(GlobalTimeoutError)(13.seconds) // global timeout
  //    } yield finalResult
  //
  //    work2.unsafeRun
  //
  //    //recurs: schedule recurs specified number of times
  //    //spaced: spaces actions out by specified duration [spacing between programs you are running]***
  //    val schedule1 = Schedule.recurs(3) && Schedule.duration(5.seconds)
  //
  //    def timeout[R, E, A](task: ZIO[R, E, A], duration: Duration): ZIO[R with Clock, Either[E, TimeoutError], A]
  //    = ZIO.unit.delay(duration).raceEither(task).either.flatMap {
  //      case Left(e) => ZIO.fail(Left(e))
  //      case Right(Left(_)) => ZIO.fail(Right(TimeoutError(duration)))
  //      case Right(Right(result)) => ZIO.succeed(result)
  //    }
  //
  //    val tasks = List(
  //      timeout(putStrLn("a").delay(1.second), 500.millis),
  //      timeout(putStrLn("b").delay(1.second), 500.millis),
  //      timeout(putStrLn("c").delay(1.second), 1500.millis),
  //      timeout(putStrLn("d").delay(1.second), 500.millis),
  //      timeout(putStrLn("e").delay(1.second), 500.millis))
  //
  //    val work = for {
  //      refConsecutiveTasks <- Ref.make(tasks)
  //      finalResult <- consecutively(refConsecutiveTasks).retry(schedule2)
  //    } yield finalResult
  //
  //    //work.unsafeRun
  //  }
  //
}
