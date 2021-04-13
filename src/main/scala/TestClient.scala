import cats.{ApplicativeError, Monad, MonadError}
import cats.effect.Sync
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonOf, _}
import org.http4s.client.Client
import org.http4s.{EntityDecoder, EntityEncoder, Method, Request, Response, Uri}
import cats.implicits._
import jawnfs2.JsonStreamSyntax
import monix.eval.Task
import io.circe.Json
import io.circe.jawn.CirceSupportParser.facade
import org.typelevel.jawn.{FContext, Facade, SupportParser}

class TestClient[F[_]: Sync](client: Client[F], apiUrl: Uri) {

//  implicit def entityDecoder[String](implicit decoder: Decoder[String]): EntityDecoder[Task, String] = jsonOf[Task, String]
//  implicit def entityEncoder[String](implicit encoder: Encoder[String]): EntityEncoder[Task, String] = jsonEncoderOf[Task, String]

  def getSomething(id: String) = {
    println(id)
    println(apiUrl)

    //Mistake around the path
    val test = Uri.unsafeFromString("https://api.covidtracking.com/v1/states/wa/current.json")
    val request: Request[F] = Request[F](Method.GET, test)
    //val text = apiUrl / "files" / id / s"${id}-0.txt"
    client.expect(request)
//    val x: fs2.Stream[F, Response[F]] = client.stream(request)
//    x.flatMap(_.body)
    //{x => println(x.body); x.body.chunks.parseJsonStream}
//    x.map((t: String) => {
//      println("hello!")
//      println(t); t}
//    )
    //println("do I get here?????")
    //Sync[Task].delay("hi")
  }
}