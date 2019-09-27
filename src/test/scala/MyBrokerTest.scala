import java.util.Calendar

import com.abarag4.Simulation1.{SIM, conf, createBroker, createCloudlet}
import com.abarag4.{MyCloudlet, Simulation1}
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.network.datacenter.NetworkDatacenter
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.slf4j.{Logger, LoggerFactory}

import scala.jdk.javaapi.CollectionConverters.asJava

class MyBrokerTest extends FunSuite with BeforeAndAfter {

  val SIM = "simulation1";

  //Initialize Config and Logger objects from 3rd party libraries
  val conf: Config = ConfigFactory.load("application.conf")
  val LOG: Logger = LoggerFactory.getLogger(getClass)

  before {
    val num_user = conf.getInt(SIM+".num_user")
    val calendar = Calendar.getInstance
    val trace_flag = conf.getBoolean("trace_flag")
    CloudSim.init(num_user, calendar, trace_flag)
  }

  def testCloudlet(cloudlet: MyCloudlet, exp_type: MyCloudlet.Type) : Unit = {
    LOG.debug("Testing cloudlet "+cloudlet.getCloudletId+" is "+exp_type+"..")
    assert(cloudlet.getType==exp_type)
  }

  test("MyBroker.submitMapperList") {

    val num_mappers = conf.getInt(SIM+".num_mappers")

    //Broker initialization
    val broker = createBroker()
    val brokerId = broker.getId

    val mappers = createCloudlet(brokerId, num_mappers, 0, MyCloudlet.Type.MAPPER)
    broker.submitMapperList(asJava[MyCloudlet](mappers))

    LOG.debug("Testing there all cloudlets were received..")
    assert(broker.getCloudletList.size()==num_mappers)

    val submittedCloudlets = broker.getCloudletList
    submittedCloudlets.forEach((e:MyCloudlet) => testCloudlet(e, MyCloudlet.Type.MAPPER))
  }

  test("MyBroker.submitReducerList") {

    val num_reducers = conf.getInt(SIM+".num_reducers")

    //Broker initialization
    val broker = createBroker()
    val brokerId = broker.getId

    val reducers = createCloudlet(brokerId, num_reducers, 0, MyCloudlet.Type.REDUCER)
    broker.submitReducerList(asJava[MyCloudlet](reducers))

    LOG.debug("Testing there all cloudlets were received..")
    assert(broker.getReducerArrayList.size()==num_reducers)

    val submittedCloudlets = broker.getReducerArrayList
    submittedCloudlets.forEach((e:MyCloudlet) => testCloudlet(e, MyCloudlet.Type.REDUCER))
  }

}
