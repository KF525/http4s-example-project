import cats.effect._
import monix.eval._

object Main extends TaskApp {

  /**
   * Server.stream[Task] gives us a Resoutce[Task, Server[Task]]. Resource, a monad type that is a description of how to acquire manage and cleanly release something with a lifecycle. Similar to try/finally but composable and referentially transparent. Allows tracking resource lifetime at type level (Function accepting Resource[]IO, A. parameter will acquire an release internally, Function accepting A reuses the live value) Allows guaranteed finalization.
   * The use method supplies our Resource[Server[Task]] to the given function. The resource is released as soon as the resulting F[B] is completed, whether normally or as a raised error. In this case, the server remains running until the application is stopped. As a resource, this application will clean-up only immediately prior to its shutdown.
   *
   * Pure descriptions of a computation. "This is not the computer robot assembling a cake, this is the recipe for the robot to assemble a cake." Not when the computation is constructed but when it's executed it will eventually produce a value of A, or fail with Throwable or never complete. The computation is kicked off by "unsafeRun" and should live at 'end of the world', not deep in your code.
   */
  def run(args: List[String]): Task[ExitCode] = {
    Server.stream[Task].use{ _ => Task.never doOnCancel(Task.apply(println("Stopped!"))) }
  }
}