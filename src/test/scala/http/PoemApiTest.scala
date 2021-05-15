package http
import monix.eval.Task
import org.http4s.client.dsl.Http4sClientDsl
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PoemApiTest extends AnyFlatSpec with Matchers with Http4sClientDsl[Task] {

  "getting a poem" should "return a poem from a third party" in {

  }

}
