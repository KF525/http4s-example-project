package zio

import zio.duration._

class MainSpec extends BaseSpec {

  "Main" should "start successfully" in {
    zio.Runtime.default.unsafeRun(Main2.run(List()).race(ZIO.unit.delay(10.seconds)))
  }
}
