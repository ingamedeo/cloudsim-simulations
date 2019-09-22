package com.abarag4

import java.text.DecimalFormat
import java.util
import java.util.{Calendar, LinkedList, List}

import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.provisioners.{BwProvisionerSimple, PeProvisionerSimple, RamProvisionerSimple}
import org.cloudbus.cloudsim._
import org.slf4j.{Logger, LoggerFactory}
import scala.collection.JavaConverters._

import scala.jdk.javaapi.CollectionConverters.asJava

/*
 * Simple Scala translation of first CloudSim example
 * Author: Amedeo Baragiola
 */

object Example1 {

  private val cloudletList = scala.collection.mutable.ListBuffer.empty[Cloudlet]
  private val vmlist = scala.collection.mutable.ListBuffer.empty[Vm]
  val conf = ConfigFactory.load()
  val LOG: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {
    LOG.debug("main() -> Example 1")

    val num_user = conf.getInt("example1.num_user") // number of cloud users
    val calendar = Calendar.getInstance // Calendar whose fields have been initialized with the current date and time.
    val trace_flag = conf.getBoolean("trace_flag") // trace events

    CloudSim.init(num_user, calendar, trace_flag)
    LOG.info("Cloudsim framework initialized")

    val datacenter0 = createDatacenter("Datacenter_0")
    LOG.info("datacenter 0 initialized")

    // Third step: Create Broker
    val broker = createBroker()
    val brokerId = broker.getId

    val vmid = 0
    val mips = conf.getInt("example1.vm.mips")
    val size = conf.getInt("example1.vm.size") // image size (MB)
    val ram = conf.getInt("example1.vm.ram") // vm memory (MB)
    val bw = conf.getInt("example1.vm.bw")
    val pesNumber = conf.getInt("example1.vm.pesNumber") // number of cpus
    val vmm = conf.getString("example1.vm.vmm") // VMM name

    // create VM
    val vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared)
    LOG.info("VM"+vmid+" (mips="+mips+",size="+size+",ram="+ram+",bw="+bw+",numCPUS="+pesNumber+") created")

    // add the VM to the vmList
    vmlist += vm

    // submit vm list to the broker
    broker.submitVmList(asJava[Vm](vmlist))

    /* CloudLet 0 */
    // Cloudlet properties
    val id = 0
    val length = conf.getInt("example1.cloudlet.length")
    val fileSize =  conf.getInt("example1.cloudlet.fileSize")
    val outputSize = conf.getInt("example1.cloudlet.outputSize")
    val utilizationModel = new UtilizationModelFull

    val cloudlet: Cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel)
    LOG.info("Cloudlet"+id+" (length="+length+",fileSize="+fileSize+",outputSize="+outputSize+") created")

    //Assign Cloudlet to VM
    cloudlet.setUserId(brokerId)
    cloudlet.setVmId(vmid)

    // add the cloudlet to the list
    cloudletList += cloudlet

    // submit cloudlet list to the broker
    broker.submitCloudletList(asJava[Cloudlet](cloudletList))

    LOG.info("Starting simulation")
    // Start the simulation
    CloudSim.startSimulation()

    // Stop simulation
    CloudSim.stopSimulation()
    LOG.info("Simulation finished, preparing results...")

    //Print results when simulation is over
    val newList: java.util.List[Cloudlet] = broker.getCloudletReceivedList[Cloudlet]

    printCloudletList(newList)
  }

  private def createBroker() : DatacenterBroker = {
    val broker = new DatacenterBroker("Broker")
    return broker
  }

  def createDatacenter(name:String) : Datacenter = {

    val hostList = scala.collection.mutable.ListBuffer.empty[Host]
    val peList = scala.collection.mutable.ListBuffer.empty[Pe] // CPUs/Cores.

    val mips = conf.getInt("example1.host.mips")

    //Add to ListBuffer
    peList += new Pe(0, new PeProvisionerSimple(mips))

    val hostId = 0
    val ram = conf.getInt("example1.host.ram") // host memory (MB)
    val storage = conf.getInt("example1.host.storage") // host storage
    val bw = conf.getInt("example1.host.bw")

    hostList += new Host(hostId,
      new RamProvisionerSimple(ram),
      new BwProvisionerSimple(bw),
      storage,
      asJava[Pe](peList),
      new VmSchedulerTimeShared(asJava[Pe](peList)))

    val arch = conf.getString("example1.datacenter.arch")
    val os = conf.getString("example1.datacenter.os")
    val vmm = conf.getString("example1.datacenter.vmm")
    val time_zone = conf.getDouble("example1.datacenter.time_zone") // time zone this resource located
    val cost = conf.getDouble("example1.datacenter.cost") // the cost of using processing in this resource
    val costPerMem = conf.getDouble("example1.datacenter.costPerMem") // the cost of using memory in this resource
    val costPerStorage = conf.getDouble("example1.datacenter.costPerStorage") // the cost of using storage in this
    // resource
    val costPerBw = conf.getDouble("example1.datacenter.costPerBw") // the cost of using bw in this resource

    val storageList = scala.collection.mutable.ListBuffer.empty[Storage] // we are not adding SAN
    val characteristics = new DatacenterCharacteristics(arch, os, vmm, asJava[Host](hostList), time_zone, cost, costPerMem, costPerStorage, costPerBw)

    val datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(asJava[Host](hostList)), asJava[Storage](storageList), 0)

    return datacenter
  }

  val indent = "    "
  val dft = new DecimalFormat("###.##")
  private def printSingleResultLine(cloudlet: Cloudlet): Unit = {
    if (cloudlet.getCloudletStatus == Cloudlet.SUCCESS) {
      LOG.info(indent + cloudlet.getCloudletId + indent + indent + "SUCCESS" + indent + indent + cloudlet.getResourceId + indent + indent + indent + cloudlet.getVmId + indent + indent + dft.format(cloudlet.getActualCPUTime) + indent + indent + dft.format(cloudlet.getExecStartTime) + indent + indent + dft.format(cloudlet.getFinishTime))
    } else {
      LOG.info(indent + cloudlet.getCloudletId + indent + indent)
    }
  }

  private def printCloudletList(list: util.List[Cloudlet]): Unit = {
    LOG.info("");
    LOG.info("========== OUTPUT ==========")
    LOG.info("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time")

    //Convert list from Java object to Scala
    val resultList = asScala(list)

    //Call list item print function (functional programming technique)
    resultList.foreach(printSingleResultLine)
  }
}
