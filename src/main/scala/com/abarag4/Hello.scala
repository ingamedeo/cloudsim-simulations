package com.abarag4

import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}

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
  }
}
