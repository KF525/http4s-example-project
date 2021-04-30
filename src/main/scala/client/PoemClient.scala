package client

import cats.effect.Sync
import io.circe.Json
import model.{CovidState, Poem}
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.{EntityDecoder, Method, Request, Uri}

class PoemClient[F[_]: Sync](client: Client[F], baseUri: Uri) {

  def getRandomPoem: F[List[Poem]] = {
    implicit val decoder: EntityDecoder[F, List[Poem]] = jsonOf[F, List[Poem]]
    client.expect[List[Poem]](Request[F](Method.GET, baseUri / "random" / "1"))
  }

  def getSomething(state: String): F[CovidState] = {
    val baseUri = Uri.unsafeFromString("https://api.covidtracking.com/v1/")
    implicit val decoder: EntityDecoder[F, CovidState] = jsonOf[F, CovidState]
    client.expect[CovidState](Request[F](Method.GET, baseUri / "states" / state / "current.json"))
  }

  //TODO: Select specific poem / Select random poem by specific poet / select random poem
  // model.Poem object:
  def getPoetry: F[Json] = {
    val baseUri = Uri.unsafeFromString("https://poetrydb.org")
    client.expect[Json](Request[F](Method.GET, baseUri / "author,title" / "Shakespeare;Sonnet"))
  }

//  /**
//   * Get all poems that contain the line passed as parameter
//   * @param line
//   * @return
//   */
  def getLine(line: String): F[Json] = {
    val baseUri = Uri.unsafeFromString("https://poetrydb.org")
    client.expect[Json](Request[F](Method.GET, baseUri / "lines" / line))
  }
//  def getLine(keyword: String): F[Poem] = {
//  implicit val decoder: EntityDecoder[F, Poem] = jsonOf[F, Poem]
//  val baseUri = Uri.unsafeFromString("https://poetrydb.org")
//  client.expect[Poem](Request[F](Method.GET, baseUri / "lines" / keyword))
//  }
}

/*
Submits a request and decodes the response on success. On failure, the status code is returned. The underlying HTTP connection is closed at the completion of the decoding.
 */