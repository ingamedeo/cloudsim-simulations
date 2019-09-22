import java.util.Calendar

import com.abarag4.Example1
import com.abarag4.Example1.{LOG, conf}
import org.cloudbus.cloudsim.core.CloudSim
import org.scalatest.FunSuite

class Example1Test extends FunSuite {

  test("Example1.main") {
    LOG.debug("main() -> Example 1")

    val num_user = conf.getInt("example1.num_user") // number of cloud users
    val calendar = Calendar.getInstance // Calendar whose fields have been initialized with the current date and time.
    val trace_flag = conf.getBoolean("trace_flag") // trace events

    CloudSim.init(num_user, calendar, trace_flag)
    LOG.info("Cloudsim framework initialized")
  }

  test("Example1.createDatacenter") {
    val datacenter0 = Example1.createDatacenter("Datacenter_0")
    assert(datacenter0 !== null)
  }
}
