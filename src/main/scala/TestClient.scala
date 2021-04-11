import cats.{ApplicativeError, Monad, MonadError}
import cats.effect.Sync
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonOf, _}
import org.http4s.client.Client
import org.http4s.{EntityDecoder, EntityEncoder, Uri}
import cats.implicits._

class TestClient[F[_]: Sync : Monad](client: Client[F], apiUrl: Uri) {

//  implicit def entityDecoder[String](implicit decoder: Decoder[String]): EntityDecoder[F, String] = jsonOf[F, String]
//  implicit def entityEncoder[String](implicit encoder: Encoder[String]): EntityEncoder[F, String] = jsonEncoderOf[F, String]

  def getSomething(id: String): F[String] = {
    println(id)
    println(apiUrl)
    val test = Uri(path = "https://api.covidtracking.com/v1/states/wa/current.json")
    //val text = apiUrl / "files" / id / s"${id}-0.txt"
    val x: F[String] = client.expect[String](test)
    x.map(t => {
      println(t); t}
    )
    // Sync[F].delay("hi")
  }
}