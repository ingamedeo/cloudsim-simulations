package com.abarag4

import java.util.Calendar

import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}
import org.cloudbus.cloudsim.Cloudlet
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.Datacenter
import org.cloudbus.cloudsim.DatacenterBroker
import org.cloudbus.cloudsim.DatacenterCharacteristics
import org.cloudbus.cloudsim.Host
import org.cloudbus.cloudsim.Log
import org.cloudbus.cloudsim.Pe
import org.cloudbus.cloudsim.Storage
import org.cloudbus.cloudsim.UtilizationModel
import org.cloudbus.cloudsim.UtilizationModelFull
import org.cloudbus.cloudsim.Vm
import org.cloudbus.cloudsim.VmAllocationPolicySimple
import org.cloudbus.cloudsim.VmSchedulerTimeShared
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple

object Hello {

  val LOG: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {
    println("Hello, world")

    val conf = ConfigFactory.load();
    val testParam = conf.getInt("hw1.testParam");
    println(testParam)

    LOG.trace("Hello World!")
    LOG.debug("How are you today?")
    LOG.info("I am fine.")
    LOG.warn("I love programming.")
    LOG.error("I am programming.")

    val num_user = 1 // number of cloud users
    val calendar = Calendar.getInstance // Calendar whose fields have been initialized with the current date and time.
    val trace_flag = false // trace events

    CloudSim.init(num_user, calendar, trace_flag)

  }
}
