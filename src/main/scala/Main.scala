import monix.eval.instances.CatsConcurrentEffectForTask
import cats.effect._
import monix.eval._
import monix.execution.Scheduler.Implicits.global

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    implicit val taskOptions: Task.Options = Task.defaultOptions
    implicit val monixEffect: CatsConcurrentEffectForTask = new CatsConcurrentEffectForTask()

    Server.stream[Task].compile.drain.as(ExitCode.Success).to[IO]
  }
}

/*
https://www.youtube.com/watch?v=83pXEdCpY4A
Referential Transparency - property of expressions in code (the result of any expression can be replaced by its definition without changing the meaning). Benefits: Understand what code is doing without needing to look at the rest of the codebase; harder to break code.
CatsEffect - functional side effect library

IO - Main interface. Pure descriptions of a computation. "This is not the computer robot assembling a cake, this is the recipe for the robot to assemble a cake." Not when the computation is constructed but when it's executed it will eventually produce a value of A, or fail with Throwable or never complete. The computation is kicked off by "unsafeRun" and should live at 'end of the world', not deep in your code. [IO vs. Future - IO looks a little like a Future but some important differences: IO is a value that describes an action. Future is a handle to an action that has already started (eager). IO optimized for throughput. Future optimized for fairness. Futures are not cancellable.]

Resource Management - Resource[IO, A] describes the ability to initialize and release a resource. Whereas IO is description of a computation, Resource is a description of how to acquire manage and release something with a lifecycle. Similar to try/finally but composable and referentially transparent. Allows tracking resource lifetime at type level (Function accepting Resource[]IO, A. parameter will acquire an release internally, Function accepting A reuses the live value) Allows guaranteed finalization.


HTTP4S

The central concept of http4s-dsl is pattern matching. An HttpRoutes[F] is declared as a simple series of case statements. Each case statement attempts to match and optionally extract from an incoming Request[F]. The code associated with the first matching case is used to generate a F[Response[F]].

Where is our Response[F]? It hasn’t been created yet. We wrapped it in an IO. In a real service, generating a Response[F] is likely to be an asynchronous operation with side effects, such as invoking another web service or querying a database, or maybe both. Operating in a F gives us control over the sequencing of operations and lets us reason about our code like good functional programmers. It is the HttpRoutes[F]’s job to describe the task, and the server’s job to run it.
*/