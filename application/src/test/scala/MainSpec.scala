import zio.ZIO
import zio.duration._

class MainSpec extends BaseSpec {

  "Main" should "start successfully" in {
    zio.Runtime.default.unsafeRun(Main.run(List()).race(ZIO.unit.delay(10.seconds)))
  }
}
