import cats.data.{Kleisli, OptionT}
import com.typesafe.scalalogging.StrictLogging
import error.CompoundPoemFailure
import error.CompoundPoemFailure.AuthenticationFailure
import model.{Email, User}
import org.http4s.server.AuthMiddleware
import org.http4s.util.CaseInsensitiveString
import org.http4s._
import zio.Task
import zio.interop.catz.monadErrorInstance
import ziohelpers.ZioLoggerSyntax._

object AuthenticationService extends StrictLogging {

  def authUser: Kleisli[Task, Request[Task], Either[CompoundPoemFailure, User]] =
    Kleisli { request: Request[Task] =>
      val header: Option[Header] = request.headers.get(CaseInsensitiveString("x-forwarded-host"))
      header match {
        case Some(h) => getAuthUserFromHeader(h.value).map(_.toRight(AuthenticationFailure("nope")))
        case None => Task(Left(AuthenticationFailure("nope")))
      }
    }

  def getAuthUserFromHeader(authHeader: String): Task[Option[User]]= {
    logger.infoZ(s"Getting user from $authHeader")
    Task {
      // TODO validate auth header, decode auth header, find user from auth user id(e.g from db)
      Option(User("dummy", "user", Email("dummy@user.com")))
    }
  }

  def onFailure: AuthedRoutes[CompoundPoemFailure, Task] =
    Kleisli { req: AuthedRequest[Task, CompoundPoemFailure] =>
      OptionT.pure[Task](
        Response[Task](status = Status.Unauthorized)
      )
    } //OptionT.liftF { AuthenticationFailure }) instead of pure

  val authMiddleware: AuthMiddleware[Task, User] = AuthMiddleware(authUser, onFailure)
}