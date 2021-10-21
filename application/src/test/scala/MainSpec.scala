import util.BaseSpec
import zio.duration._
import ziotestsyntax.ZioTestSyntax.ZioTestHelper

class MainSpec extends BaseSpec {

  //TODO: Fix this test
  "Main" should "start successfully" in {
    Main.run(List()).timeout(5.seconds).unsafeRun should be (None)
  }
}
