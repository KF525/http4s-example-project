import cats.effect.Sync
import io.circe.Json
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.{Method, Request, Uri}

class TestClient[F[_]: Sync](client: Client[F], baseUri: Uri) {

  def getSomething(state: String): F[Json] =
    client.expect(Request[F](Method.GET, baseUri / "states" / state / "current.json"))

  def getMobyDick: F[String] = {
    val uri:Uri = Uri.unsafeFromString("https://www.gutenberg.org")
    println(uri)
    client.expect(Request[F](Method.GET, uri / "files" / "2701"/ "2701-0.txt"))
  }
}