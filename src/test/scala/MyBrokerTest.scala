import java.util.Calendar

import com.abarag4.{MyCloudlet}
import com.abarag4.Simulation1.{createBroker, createCloudlet, createDatacenters, createVM}
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.Vm
import org.cloudbus.cloudsim.core.{CloudSim, CloudSimTags, SimEvent}
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.slf4j.{Logger, LoggerFactory}

import scala.jdk.javaapi.CollectionConverters.asJava

class MyBrokerTest extends FunSuite with BeforeAndAfter {

  val SIM = "simulation1";

  //Initialize Config and Logger objects from 3rd party libraries
  val conf: Config = ConfigFactory.load("simulation1.conf")
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

  /**
   * This test is to show the functioning on the processEvent method.
   * For this purpose a sample simulation with 1 DC, 1 VM and only 3 mappers is run, at the end it is checked that the last submitted cloudlet is a REDUCER.
   * */
  test("MyBroker.processEvent") {

    LOG.debug("Testing processEvent method..")

    //Recursive call for datacenter generation
    val datacenters = createDatacenters(1)

    //Broker initialization
    val broker = createBroker()
    val brokerId = broker.getId

    //Recursive call for VM creation
    val vmlist = createVM(brokerId, 3)
    //Set list of available VMs on broker
    broker.submitVmList(asJava[Vm](vmlist))

    val mappers = createCloudlet(brokerId, 3, 0, MyCloudlet.Type.MAPPER)
    val reducers = createCloudlet(brokerId, 1, 3, MyCloudlet.Type.REDUCER)

    broker.submitMapperList(asJava(mappers))
    broker.submitReducerList(asJava(reducers))

    CloudSim.startSimulation()
    CloudSim.stopSimulation()

    val recCloudlets = broker.getCloudletReceivedList

    val lastCloudlet = recCloudlets.get(recCloudlets.size()-1).asInstanceOf[MyCloudlet]

    LOG.debug("Check last cloudlet submitted is a REDUCER..")
    assert(lastCloudlet.getType==MyCloudlet.Type.REDUCER)
  }

}
