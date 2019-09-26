package com.abarag4

import java.text.DecimalFormat
import java.util
import java.util.Calendar

import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.provisioners.{BwProvisionerSimple, PeProvisionerSimple, RamProvisionerSimple}
import org.cloudbus.cloudsim.{Cloudlet, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared, Datacenter, DatacenterBroker, DatacenterCharacteristics, Host, HostDynamicWorkload, Pe, Storage, UtilizationModelFull, Vm, VmAllocationPolicySimple, VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.slf4j.{Logger, LoggerFactory}

//import scala.collection.JavaConverters.asScala
import scala.jdk.javaapi.CollectionConverters.asScala
import scala.jdk.javaapi.CollectionConverters.asJava
import org.cloudbus.cloudsim.NetworkTopology
import org.cloudbus.cloudsim.network.datacenter.NetworkDatacenter
import org.cloudbus.cloudsim.power.{PowerHost, PowerHostUtilizationHistory}
import org.cloudbus.cloudsim.power.models.PowerModelLinear

/**
 * This is the first Simulation provided.
 *
 * //todo: additional details?
 *
 * All simulations show a run of a Map/Reduce algorithm implementation as further detailed in the README.md file.
 *
 * The code is structured in such a way that all parameters can be specified from the application.conf file.
 *
 * The code entry point is the main method.
 *
 * Multiple utility methods are present:
 * - createDatacenter(name:String, startId: Int) : NetworkDatacenter
 * - createDatacenters(numDc: Int) : List[NetworkDatacenter]
 * - createDatacentersImpl(numDc: Int, startId: Int, dcList: scala.collection.mutable.ListBuffer[NetworkDatacenter]
 * - createCPUs(num: Int, startId: Int, mips: Int, cpuList: scala.collection.mutable.ListBuffer[Pe]) : Unit
 * - createHostImpl(num: Int, startId: Int, hostList: scala.collection.mutable.ListBuffer[Host]) : Unit
 * - createVMImpl(userId: Int, num: Int, startId: Int, vmList: scala.collection.mutable.ListBuffer[Vm]): Unit
 * - createVM(userId: Int, vms: Int) : List[Vm]
 * - createBroker() : DatacenterBroke
 * - createCloudletImpl(userId: Int, num: Int, cloudletType: MyCloudlet.Type, startId: Int, cloudletList: scala.collection.mutable.ListBuffer[MyCloudlet]):Unit
 * - createCloudlet(userId: Int, cloudlets: Int, cloudletType: MyCloudlet.Type) : List[MyCloudlet]
 * - printSingleResultLine(cloudlet: Cloudlet): Unit
 * - printCloudletList(list: List[MyCloudlet]): Unit
 *
 */

object Simulation1 {

  val conf: Config = ConfigFactory.load()
  val LOG: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val startTime = System.currentTimeMillis
    LOG.debug("Starting simulation 1..")

    val num_user = conf.getInt("sim1.num_user")
    val calendar = Calendar.getInstance
    val trace_flag = conf.getBoolean("trace_flag")
    val num_vms = conf.getInt("sim1.num_vms")
    val num_dcs = conf.getInt("sim1.num_dcs")

    CloudSim.init(num_user, calendar, trace_flag)
    LOG.info("Cloudsim framework initialized")

    val datacenters = createDatacenters(num_dcs)

    // Third step: Create Broker
    val broker = createBroker()
    val brokerId = broker.getId

    val vmlist = createVM(brokerId, num_vms)

    //Create VM map from list
    //todo: code left here
    val vmMap = vmlist.map(v => Map(v.getId -> v)).reduce(_++_)

    // submit VM list to the broker
    broker.submitVmList(asJava[Vm](vmlist))
    val mappers = createCloudlet(brokerId, 12, 0, MyCloudlet.Type.MAPPER)
    val reducers = createCloudlet(brokerId, 4, 13, MyCloudlet.Type.REDUCER)

    // submit cloudlet list to the broker
    broker.submitMapperList(asJava[MyCloudlet](mappers))
    broker.submitReducerList(asJava[MyCloudlet](reducers))

    //val cloudletlist = mappers ++ reducers
    //broker.submitCloudletList(asJava[MyCloudlet](mappers))

    //NetworkTopology.addLink(datacenter0.getId, broker.getId, 1000.0, 10)
    //NetworkTopology.addLink(datacenter1.getId, broker.getId, 1000.0, 10)
    //NetworkTopology.addLink(datacenters(0).getId, datacenters(1).getId, 10000.0, 10)

    LOG.info("Starting simulation")
    // Start the simulation
    CloudSim.startSimulation()

    // Stop simulation
    CloudSim.stopSimulation()
    LOG.info("Simulation finished, preparing results...")

    //Print results when simulation is over
    val newList: List[MyCloudlet] = asScala(broker.getCloudletReceivedList[MyCloudlet]).toList

    printCloudletList(newList)

    LOG.debug("N. Hosts: "+datacenters(0).getHostList.size())
    LOG.debug("N. Hosts: "+datacenters(1).getHostList.size())

    LOG.info("# CloudSimExample6 finished!")
    val endTime = System.currentTimeMillis
    val totalTimeTaken = (endTime - startTime) / 1000.0
    LOG.info("The total time taken for the execution: " + totalTimeTaken + " s.")
  }

  def createCPUs(num: Int, startId: Int, mips: Int, cpuList: scala.collection.mutable.ListBuffer[Pe]) : Unit = {

    if (num==0) {
      return
    }

    cpuList += new Pe(startId, new PeProvisionerSimple(mips))

    createCPUs(num-1, startId+1, mips, cpuList)
  }

  @scala.annotation.tailrec
  def createHostImpl(num: Int, startId: Int, hostList: scala.collection.mutable.ListBuffer[Host]) : Unit = {
    val mips = conf.getInt("sim1.host.mips")
    val numCPU = conf.getInt("sim1.host.numCpu")

    //Base case
    if (num==0) {
      return
    }

    val peList = scala.collection.mutable.ListBuffer.empty[Pe] // CPUs/Cores.

    //Create CPUs (Processing Units)
    createCPUs(numCPU, 0, mips, peList)

    val ram = conf.getInt("sim1.host.ram") // host memory (MB)
    val storage = conf.getInt("sim1.host.storage") // host storage
    val bw = conf.getInt("sim1.host.bw")
    val diskSpeed = conf.getDouble("sim1.host.diskSpeed") //todo: finish adding disk speed

    val host: MyHost = new MyHost(startId,
      new RamProvisionerSimple(ram),
      new BwProvisionerSimple(bw),
      storage,
      asJava[Pe](peList),
      new VmSchedulerTimeShared(asJava[Pe](peList)))

    host.setDiskSpeed(diskSpeed)

    hostList += host

    createHostImpl(num-1, startId+1, hostList)
  }

  def createDatacenters(numDc: Int) : List[NetworkDatacenter] = {

    val dclist = scala.collection.mutable.ListBuffer.empty[NetworkDatacenter]

    createDatacentersImpl(numDc, 0, dclist)

    return dclist.toList
  }

  @scala.annotation.tailrec
  def createDatacentersImpl(numDc: Int, startId: Int, dcList: scala.collection.mutable.ListBuffer[NetworkDatacenter]) : Unit = {

    if(numDc==0) {
      return
    }

    val datacenter = createDatacenter("datacenter"+startId, startId)

    dcList += datacenter

    createDatacentersImpl(numDc-1, startId+1, dcList)
  }

  //This method dynamically builds a datacenter
  def createDatacenter(name:String, startId: Int) : NetworkDatacenter = {

    val hostList = scala.collection.mutable.ListBuffer.empty[Host]

    //Read number of hosts to put in this DC from conf
    val numHosts = conf.getInt("sim1."+name+".numHosts")

    createHostImpl(numHosts, startId, hostList)

    val arch = conf.getString("sim1."+name+".arch")
    val os = conf.getString("sim1."+name+".os")
    val vmm = conf.getString("sim1."+name+".vmm")
    val time_zone = conf.getDouble("sim1."+name+".time_zone") // time zone this resource located
    val cost = conf.getDouble("sim1."+name+".cost") // the cost of using processing in this resource
    val costPerMem = conf.getDouble("sim1."+name+".costPerMem") // the cost of using memory in this resource
    val costPerStorage = conf.getDouble("sim1."+name+".costPerStorage") // the cost of using storage in this
    // resource
    val costPerBw = conf.getDouble("sim1."+name+".costPerBw") // the cost of using bw in this resource

    val storageList = scala.collection.mutable.ListBuffer.empty[Storage] // we are not adding SAN
    val characteristics = new DatacenterCharacteristics(arch, os, vmm, asJava[Host](hostList), time_zone, cost, costPerMem, costPerStorage, costPerBw)

    val datacenter = new NetworkDatacenter(name, characteristics, new VmAllocationPolicySimple(asJava[Host](hostList)), asJava[Storage](storageList), 0)

    return datacenter
  }

  @scala.annotation.tailrec
  def createVMImpl(userId: Int, num: Int, startId: Int, vmList: scala.collection.mutable.ListBuffer[Vm]): Unit = {

    //Base case
    if (num==0) {
      return
    }

    val mips = conf.getInt("sim1.vm.mips")
    val size = conf.getInt("sim1.vm.size") // image size (MB)
    val ram = conf.getInt("sim1.vm.ram") // vm memory (MB)
    val bw = conf.getInt("sim1.vm.bw")
    val pesNumber = conf.getInt("sim1.vm.pesNumber") // number of cpus
    val vmm = conf.getString("sim1.vm.vmm") // VMM name

    // create VM
    //val vm = new Vm(startId, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared) //sequenziati
    val vm = new Vm(startId, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared) //sequenziati
    LOG.info("VM"+startId+" (mips="+mips+",size="+size+",ram="+ram+",bw="+bw+",numCPUS="+pesNumber+") created")

    //Add VM to list
    vmList += vm

    createVMImpl(userId, num-1, startId+1, vmList)
  }

  private def createVM(userId: Int, vms: Int) : List[Vm] = {

    val vmlist = scala.collection.mutable.ListBuffer.empty[Vm]

    createVMImpl(userId, vms, 0, vmlist)

    return vmlist.toList
  }

  private def createBroker() : MyBroker = {
    val broker = new MyBroker("Broker")
    return broker
  }

  @scala.annotation.tailrec
  private def createCloudletImpl(userId: Int, num: Int, cloudletType: MyCloudlet.Type, startId: Int, cloudletList: scala.collection.mutable.ListBuffer[MyCloudlet]):Unit = {

    if (num==0) {
      return
    }

    // Cloudlet properties
    val pesNumber = conf.getInt("sim1.cloudlet.pesNumber") // number of cpus
    val length = conf.getInt("sim1.cloudlet.length")
    val fileSize =  conf.getInt("sim1.cloudlet.fileSize")
    val outputSize = conf.getInt("sim1.cloudlet.outputSize")
    val utilizationModel = new UtilizationModelFull

    //Todo: this is dead code, remove it
    /*
    if (startId % 5 == 0 && startId!=0) {
      val cloudlet: MyCloudlet = new MyCloudlet(startId, length*2, pesNumber, fileSize, outputSize, utilizationModel, new OurUtilizationModel, utilizationModel)
      LOG.info("Cloudlet"+startId+" (length="+length+",fileSize="+fileSize+",outputSize="+outputSize+") created")
      cloudlet.setUserId(userId)
      cloudlet.setType(cloudletType) //Set custom type

      cloudletList += cloudlet
    } else {
      val cloudlet: MyCloudlet = new MyCloudlet(startId, length, pesNumber, fileSize, outputSize, utilizationModel, new OurUtilizationModel, utilizationModel)
      LOG.info("Cloudlet"+startId+" (length="+length+",fileSize="+fileSize+",outputSize="+outputSize+") created")
      cloudlet.setUserId(userId)
      cloudlet.setType(cloudletType) //Set custom type

      cloudletList += cloudlet
    }
    */

    val cloudlet: MyCloudlet = new MyCloudlet(startId, length*2, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel)
    LOG.info("Cloudlet"+startId+" (length="+length+",fileSize="+fileSize+",outputSize="+outputSize+") created")
    cloudlet.setUserId(userId)
    cloudlet.setType(cloudletType) //Set custom type

    cloudletList += cloudlet

    createCloudletImpl(userId, num-1, cloudletType, startId+1, cloudletList)
  }

  def createCloudlet(userId: Int, cloudlets: Int, startId: Int, cloudletType: MyCloudlet.Type) : List[MyCloudlet] = {

    val cloudletlist = scala.collection.mutable.ListBuffer.empty[MyCloudlet]

    createCloudletImpl(userId, cloudlets, cloudletType, startId, cloudletlist)

    return cloudletlist.toList
  }

  val indent = "    "
  val dft = new DecimalFormat("###.##")
  private def printSingleResultLine(cloudlet: Cloudlet): Unit = {
    if (cloudlet.getCloudletStatus == Cloudlet.SUCCESS) {

      val costPerSec = cloudlet.getCostPerSec(cloudlet.getResourceId) *  cloudlet.getActualCPUTime()
      val ramUtilization = cloudlet.getUtilizationOfRam(0)
      //LOG.info("costPerSec: "+costPerSec)
      //LOG.info("ramUtilization: "+ramUtilization)

      LOG.info(indent + cloudlet.getCloudletId + indent + indent + "  SUCCESS" + indent + indent + cloudlet.getResourceId + indent + indent + indent + cloudlet.getVmId + indent + indent + dft.format(cloudlet.getActualCPUTime) + indent + indent + dft.format(cloudlet.getExecStartTime) + indent + indent + dft.format(cloudlet.getFinishTime) + indent + indent + dft.format(cloudlet.getSubmissionTime) + indent + indent + dft.format(cloudlet.getCostPerSec))
    } else {
      LOG.info(indent + cloudlet.getCloudletId + indent + indent)
    }
  }

  private def printCloudletList(list: List[MyCloudlet]): Unit = {
    LOG.info("");
    LOG.info("========== OUTPUT ==========")
    LOG.info("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time")

    //Call list item print function (functional programming technique)
    list.foreach(printSingleResultLine)
  }
}
